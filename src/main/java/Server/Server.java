package Server;

import Car.Car;
import ChargeStation.ChargeStation;
import ChargeStation.FastChargeStation;
import ChargeStation.SlowChargeStation;
import Message.*;
import UserManagement.UserManager;
import WaitingZone.WaitingZone;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Server {
    public static ConcurrentLinkedDeque<String> MessageQueue = new ConcurrentLinkedDeque<>();; //消息队列。每个消息是一个JSON字符串
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
    public boolean CancelCharging_Server(Car car) {
        if (car.isFastCharging()) {
            if (waitingZone.contains(car)) {
                return waitingZone.CancelCharging_Waiting(car);
            }else {
                for (FastChargeStation fastStation : FastStations) {
                    if (fastStation.contains(car)) {
                        return fastStation.CancelCharging(car);
                    }
                }
            }
        }else {
            if (waitingZone.contains(car)) {
                return waitingZone.CancelCharging_Waiting(car);
            }else {
                for (SlowChargeStation slowStation : SlowStations) {
                    if (slowStation.contains(car)) {
                        return slowStation.CancelCharging(car);
                    }
                }
            }
        }
        return false;
    }
    public boolean ChangeChargeMode_Server(Car car) {

    }
    public void run() {
        this.MessageProcessing();
    }
    public void MessageProcessing() {
        Gson gson = new Gson();
        while (!StopServer) {
            while (!MessageQueue.isEmpty()) {
                String JsonMsg = MessageQueue.removeFirst();
                if (JsonMsg == null) {
                    throw new NullPointerException("NULL Message");
                }
                Message message = gson.fromJson(JsonMsg, Message.class);
                switch (message.Type) {
                    case "Enter_Waiting_Zone":
                        //TODO：加入进入等候区的函数，可能需要进行修改
                        msg_EnterWaitingZone m = gson.fromJson(JsonMsg, msg_EnterWaitingZone.class);
                        boolean success =  waitingZone.JoinWaitingZone(m.UserCar);
                        if (success) {//如果加入成功，尝试一次调度
                            Schedule();
                            //TODO 给客户端返回一个加入等候区成功的消息。这个True代表加入等候区成功，不代表其他事情。
                        }
                        break;
                    case "Charging_Complete":
                        //TODO 充电完成，进行一次调度。充电那边需要生成详单，以及结算费用。以及往消息队列里插入消息
                        Schedule();
                        break;
                    case "Cancel_Charging":
                        msg_CancelCharging cancelCharging = gson.fromJson(JsonMsg, msg_CancelCharging.class);
                        boolean cancelChargingServer = CancelCharging_Server(cancelCharging.UserCar);
                        Schedule();
                        //TODO 取消充电，并进行一次充电的调度。将取消的结果返回客户端
                        break;
                    case "Check_Charging_Form":
                        //TODO 计算充电详单
                        break;
                    case "User_Registration":
                        msg_UserRegistration msgUserRegistration = gson.fromJson(JsonMsg, msg_UserRegistration.class);
                        //TODO 用户注册。并把结果返回客户端
                        boolean Result = UserManager.UserRegistration(msgUserRegistration.UserName, msgUserRegistration.UserPassword);
                        break;
                    case "User_Login":
                        //TODO 用户登录、并把结果返回客户端
                        msg_UserLogin msgUserLogin = gson.fromJson(JsonMsg, msg_UserLogin.class);
                        boolean Login_Success = UserManager.UserLogIn(msgUserLogin.UserName, msgUserLogin.UserPassword);
                        break;
                    case "Change_Charging_Mode":
                        //TODO 改变充电模式
                        break;
                    case "Change_Charge_Capacity":
                        //TODO 改变充电电量
                        break;
                    case "Check_Sequence_Num":
                        //TODO 查看排队号码
                        break;
                    case "Check_Forward_CarAmount":
                        //TODO 检查前面还有多少辆车在排队
                        break;
                    case "Turn_On_Station":
                        //TODO 打开充电桩
                        break;
                    case "Turn_Off_Station":
                        //TODO 关闭充电桩
                        break;
                    case "Check_All_Station_State":
                        //TODO 检查全部充电桩的状态
                        break;
                    case "Check_Station_Info":
                        //TODO 检查某个充电桩等候服务的车辆信息
                        break;
                    case "Show_Station_Table":
                        //TODO 报表展示
                        break;
                    case "Station_Recovery":
                        //TODO 充电桩故障恢复
                        break;
                    case "Station_Fault":
                        //TODO 充电桩故障
                        break;
                    default:
                        throw new IllegalArgumentException("Message Type does NOT Exist");
                }
            }
        }
    }
    public void StopServer() {
        StopServer = true;
    }
    //会有单独一个线程负责做调度的工作
    public void Schedule() {
        if (!StopServer) {
            if (waitingZone.isOnService() && !waitingZone.isEmpty()) {
                /*
                  每次只调度最多一辆慢充车和一辆快充车。
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
