package Server;

import Car.Car;
import ChargeStation.ChargeStation;
import ChargeStation.FastChargeStation;
import ChargeStation.SlowChargeStation;
import WaitingZone.WaitingZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Server {
    private boolean StopServer;
    private static final Logger logger = LogManager.getLogger(Server.class);
    private WaitingZone waitingZone;
    private List<FastChargeStation> FastStations;
    private List<SlowChargeStation> SlowStations;

    public Server(int FastStationCount, int SlowStationCount) {
        StopServer = false;
        FastStations = new ArrayList<>(FastStationCount);
        SlowStations = new ArrayList<>(SlowStationCount);
        waitingZone = new WaitingZone();
        for (int i = 0; i < FastStationCount; i++) {
            FastStations.add(new FastChargeStation());
        }
        for (int i = 0; i < SlowStationCount; i++) {
            SlowStations.add(new SlowChargeStation());
        }
    }
    public void StopServer() {
        StopServer = true;
    }
    //会有单独一个线程负责做调度的工作
    public void Schedule() {
        while (!StopServer) {
            while (waitingZone.isOnService() && !waitingZone.isEmpty()) {
                /*
                  每个循环只调度最多一辆慢充车和一辆快充车。
                  我个人觉得这是合理的，因为每调度一辆车，充电桩们的状态就发生了变化，需要重新调度
                  举个例子，一开始两个快充站AB都是空的，我们有两辆快充车。
                  我们选择最短等待时间的充电桩，这种情况下任选一个
                  如果批量调度的话，两辆车可能会被放到同一个快充站。
                  但是如果一个循环调度一辆的话，这一轮第一辆被放到A。第二路，B的等待时间小于A，第二辆车会被丢给B。
                 */
                List<FastChargeStation> fast = new ArrayList<>();
                List<SlowChargeStation> slow = new ArrayList<>();
                for (FastChargeStation fastStation : FastStations) {
                    if (fastStation.hasEmptySlot()) {
                        fast.add(fastStation);
                    }
                }
                for (SlowChargeStation slowStation : SlowStations) {
                    if (slowStation.hasEmptySlot()) {
                        slow.add(slowStation);
                    }
                }
                if (!fast.isEmpty()) {
                    Deque<Car> fastQueue = waitingZone.getFastQueue();
                    if (!fastQueue.isEmpty()) {
                        int ShortestIndex = 0;
                        for (int i = 0; i < fast.size(); i++) {
                            if (fast.get(i).getWaitingTime() < fast.get(ShortestIndex).getWaitingTime()) {
                                ShortestIndex = i;
                            }
                            fast.get(ShortestIndex).JoinFastStation(fastQueue.removeFirst());
                        }
                    }
                }
                if (!slow.isEmpty()) {
                    Deque<Car> slowQueue = waitingZone.getSlowQueue();
                    if (!slowQueue.isEmpty()) {
                        int ShortestIndex = 0;
                        for (int i = 0; i < slow.size(); i++) {
                            if (slow.get(i).getWaitingTime() < slow.get(ShortestIndex).getWaitingTime()) {
                                ShortestIndex = i;
                            }
                        }
                        slow.get(ShortestIndex).JoinSlowStation(slowQueue.removeFirst());
                    }
                }
            }
        }
    }
    public int getFastStationCount() {
        return FastStations.size();
    }
    public int getSlowStationCount() {
        return SlowStations.size();
    }
    public int getStationCount() {
        return getSlowStationCount() + getFastStationCount();
    }
    public boolean HandleStationError(ChargeStation ErrorStation, int SchedulingStrategy) {
        //只考虑单一充电桩故障，并且恰好该充电桩有车排队的情况
        if (SchedulingStrategy == WaitingZone.Priority_Scheduling) {
            waitingZone.StopService();
            Deque<Car> stationError_Cars = ErrorStation.getCarQueue();
            //如果是慢充
            if (ErrorStation.getClass() == SlowChargeStation.class) {
                while (!stationError_Cars.isEmpty()) {
                    for (int i = 0; i < SlowStations.size(); i++) {
                        if (SlowStations.get(i).hasEmptySlot()) {
                            SlowChargeStation slowChargeStation = SlowStations.get(i);
                            slowChargeStation.JoinSlowStation(stationError_Cars.removeFirst());
                        }
                    }
                }//快充站
            }else if (ErrorStation.getClass() == FastChargeStation.class){
                while (!stationError_Cars.isEmpty()) {
                    for (int i = 0; i < FastStations.size(); i++) {
                        if (FastStations.get(i).hasEmptySlot()) {
                            FastChargeStation fastChargeStation = FastStations.get(i);
                            fastChargeStation.JoinFastStation(stationError_Cars.removeFirst());
                        }
                    }
                }
            }else {
                logger.debug("Error Station Type.Neither Fast Nor Slow");
                throw new IllegalArgumentException("Error Station Type");
            }
            waitingZone.StartService();
        }else if (SchedulingStrategy == WaitingZone.Time_Sequence_Scheduling) {//时间顺序调度
            waitingZone.StopService();
            //慢充站
            if (ErrorStation.getClass() == SlowChargeStation.class) {
                ArrayList<Car> stationError_Cars = new ArrayList<Car>();
                for (SlowChargeStation slowStation : SlowStations) {
                    Deque<Car> SlowCars = slowStation.getCarQueue();
                    while (SlowCars.size() > 1) {
                        stationError_Cars.add(SlowCars.removeLast());
                    }
                }
                stationError_Cars.addAll(ErrorStation.getCarQueue());
                stationError_Cars.sort(Car::compareTo);
                while (!stationError_Cars.isEmpty()) {
                    for (int i = 0; i < SlowStations.size(); i++) {
                        if (SlowStations.get(i).hasEmptySlot()) {
                            SlowChargeStation slowChargeStation = SlowStations.get(i);
                            slowChargeStation.JoinSlowStation(stationError_Cars.remove(0));
                        }
                    }
                }//快充站
            }else if (ErrorStation.getClass() == FastChargeStation.class) {
                ArrayList<Car> stationError_Cars = new ArrayList<Car>();
                for (FastChargeStation fastStation : FastStations) {
                    Deque<Car> FastCars = fastStation.getCarQueue();
                    while (FastCars.size() > 1) {
                        stationError_Cars.add(FastCars.removeLast());
                    }
                }
                stationError_Cars.addAll(ErrorStation.getCarQueue());
                stationError_Cars.sort(Car::compareTo);
                while (!stationError_Cars.isEmpty()) {
                    for (int i = 0; i < FastStations.size(); i++) {
                        if (FastStations.get(i).hasEmptySlot()) {
                            FastChargeStation fastChargeStation = FastStations.get(i);
                            fastChargeStation.JoinFastStation(stationError_Cars.remove(0));
                        }
                    }
                }
            }else {
                logger.debug("Error Station Type.Neither Fast Nor Slow");
                throw new IllegalArgumentException("Error Station Type");
            }
            waitingZone.StartService();
        }
        return false;
    }
    public boolean changeChargeMode(Car car) {
        return waitingZone.changeChargeMode_Waiting(car);
    }
    public boolean changeChargeCapacity(Car car, double NewVal) {
        return waitingZone.changeChargeCapacity_Waiting(car, NewVal);
    }
}
