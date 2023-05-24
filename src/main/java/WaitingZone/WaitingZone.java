package WaitingZone;

import Car.Car;

import java.util.concurrent.ConcurrentLinkedDeque;

public class WaitingZone {
    private static final int MAX_SIZE = 6;
    public static final int Priority_Scheduling = 0;
    public static final int Time_Sequence_Scheduling = 1;
    private boolean isOnService;
    private ConcurrentLinkedDeque<Car> FastQueue;
    private ConcurrentLinkedDeque<Car> SlowQueue;
    private static int TotalCarCount = 0;//充电的总车次数(是车次数，不是车辆数，如果一辆车充一百次，那么车次要加一百)

    private int size;//当前等候区的车辆数，不超过6
    public WaitingZone() {
        FastQueue = new ConcurrentLinkedDeque<>();
        SlowQueue = new ConcurrentLinkedDeque<>();
        size = 0;
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
            if (size < MAX_SIZE) {
                FastQueue.addLast(car);
                size ++;
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
            if (size < MAX_SIZE) {
                SlowQueue.addLast(car);
                size ++;
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
        if (car.isFastCharging()) {
            car.setChargingMode(false);
            car.setQueueSeq(TotalCarCount);
            TotalCarCount ++;
            FastQueue.remove(car);
            return AddToSlowQueue(car);
        }else {
            car.setChargingMode(true);
            car.setQueueSeq(TotalCarCount);
            TotalCarCount ++;
            SlowQueue.remove(car);
            return AddToFastQueue(car);
        }
    }
    public boolean changeChargeCapacity_Waiting(Car car, double NewValue) {
        if (car.isFastCharging()) {
            if (FastQueue.contains(car)) {
                car.setRequestedChargingCapacity(NewValue);
                return true;
            }
        }else {
            if (SlowQueue.contains(car)) {
                car.setRequestedChargingCapacity(NewValue);
                return true;
            }
        }
        return false;
    }
    public boolean CancelCharging_Waiting(Car car) {
        if (car.isFastCharging()) {
            FastQueue.remove(car);
        }else {
            SlowQueue.remove(car);
        }
        return true;
    }
    public synchronized boolean isEmpty() {
        return size == 0;
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
    public int getForwardCarAmount(Car car) {
        if (car != null) {
            if (!this.contains(car)) {
                return -1;
            }
            if (car.isFastCharging()) {
                int F_index = 0;
                for (Car car1 : FastQueue) {
                    if (car1.equals(car)) {
                        return F_index;
                    }
                    F_index++;
                }
            } else {
                int S_index = 0;
                for (Car car1 : SlowQueue) {
                    if (car1.equals(car)) {
                        return S_index;
                    }
                    S_index++;
                }
            }
        }
        return -1;
    }
}
