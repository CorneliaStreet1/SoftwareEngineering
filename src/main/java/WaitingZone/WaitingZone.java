package WaitingZone;

import Car.Car;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedDeque;

public class WaitingZone {
    private static Logger logger = LogManager.getLogger(WaitingZone.class);
    private static final int MAX_SIZE = 6;
    public static final int Priority_Scheduling = 0;
    public static final int Time_Sequence_Scheduling = 1;
    private boolean isOnService;
    private ConcurrentLinkedDeque<Car> FastQueue;
    private ConcurrentLinkedDeque<Car> SlowQueue;
    private static int TotalCarCount = 0;//充电的总车次数(是车次数，不是车辆数，如果一辆车充一百次，那么车次要加一百)

    //private int size;//当前等候区的车辆数，不超过6
    public WaitingZone() {
        FastQueue = new ConcurrentLinkedDeque<>();
        SlowQueue = new ConcurrentLinkedDeque<>();
        //size = 0;
        isOnService = true;
    }
    public synchronized void StartService() {
        isOnService = true;
    }
    public synchronized void StopService() {
        isOnService = false;
    }
    public synchronized boolean isOnService() {
        return isOnService;
    }
    public synchronized static int getTotalCarCount() {
        return TotalCarCount;
    }
    public boolean AddToFastQueue(Car car) {
        if (car.isFastCharging()) {
            if (size() < MAX_SIZE) {
                FastQueue.addLast(car);
                //size ++;
                TotalCarCount ++;
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }
    public boolean AddToSlowQueue(Car car) {
        if (car.isFastCharging()) {
            return false;
        }else {
            if (size() < MAX_SIZE) {
                SlowQueue.addLast(car);
                //size ++;
                TotalCarCount ++;
                return true;
            }
        }
        return false;
    }
    public synchronized boolean JoinWaitingZone(Car car) {
        if (car.isFastCharging()) {
            return AddToFastQueue(car);
        }
        return AddToSlowQueue(car);
    }
    public boolean changeChargeMode_Waiting(Car car) {
        if (FastQueue.contains(car)) {
            logger.info("Car at WaitingZone - FastQueue ");
            for (Car car1 : FastQueue) {
                if (car1.equals(car)) {
                    car = car1;
                    break;
                }
            }
            FastQueue.remove(car);
            car.setChargingMode(false);
            //car.setQueueSeq(TotalCarCount);
            TotalCarCount ++;
            logger.info("Car charge Mode: FAST ==> SLOW");
            logger.info("END=============ChangeChargeMode_Server");
            return AddToSlowQueue(car);
        }else {
            logger.info("Car at WaitingZone - SlowQueue ");
            for (Car car1 : SlowQueue) {
                if (car1.equals(car)) {
                    car = car1;
                    break;
                }
            }
            SlowQueue.remove(car);
            car.setChargingMode(true);
            //car.setQueueSeq(TotalCarCount);
            TotalCarCount ++;
            logger.info("Car charge Mode: SLOW ==> FAST");
            logger.info("END=============ChangeChargeMode_Server");
            return AddToFastQueue(car);
        }
    }
    public boolean changeChargeCapacity_Waiting(Car car, double NewValue) {
        if (FastQueue.contains(car)) {
            logger.info("Car at FAST Waiting Queue");

            for (Car car1 : FastQueue) {
                if (car1.equals(car)) {
                    logger.info("Change Value: " + car1.getRequestedChargingCapacity() + " ==> " + NewValue);
                    car1.setRequestedChargingCapacity(NewValue);
                }
            }
            logger.info("END========changeChargeCapacity_Server-Waiting");
                return true;
        }
        else if (SlowQueue.contains(car)){
            logger.info("Car at SLOW Waiting Queue");

            for (Car car1 : SlowQueue) {
                if (car1.equals(car)) {
                    logger.info("Change Value: " + car1.getRequestedChargingCapacity() + " ==> " + NewValue);
                    car1.setRequestedChargingCapacity(NewValue);
                }
            }
            logger.info("END========changeChargeCapacity_Server-Waiting");
            return true;
        }
        logger.info("END========Car NOT Found");
        return false;
    }
    public boolean CancelCharging_Waiting(Car car) {
        if (FastQueue.contains(car)) {
            logger.info("Cancel Charging Success at waitingZone FastQueue " + "Car "+ car.getPrimaryKey());
            FastQueue.remove(car);
        }else {
            logger.info("Cancel Charging Success at waitingZone SlowQueue " + "Car "+ car.getPrimaryKey());
            SlowQueue.remove(car);
        }
        logger.info("End===========CancelCharging_Server() At WaitingZone");
        return true;
    }
    public synchronized boolean isEmpty() {
        return size() == 0;
    }
    public synchronized int size() {
        return FastQueue.size() + SlowQueue.size();
    }

    public ConcurrentLinkedDeque<Car> getFastQueue() {
        return FastQueue;
    }

    public ConcurrentLinkedDeque<Car> getSlowQueue() {
        return SlowQueue;
    }
    public boolean contains(Car car) {
        if (car == null) {
            return false;
        }
        for (Car car1 : FastQueue) {
            if (car.equals(car1)) {
                return true;
            }
        }
        for (Car car1 : SlowQueue) {
            if (car.equals(car1)) {
                return true;
            }
        }
        return false;
    }
}
