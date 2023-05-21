package Car;

public class Car implements Comparable<Car>{
    private static int nextPrimaryKey = 0;
    private boolean isFastCharge; //是否是快充模式
    private int QueueSeq; //到达的序号
    protected int PrimaryKey;
    private double RequestedChargingCapacity; //车辆的请求充电量
    private double CarBatteryCapacity;// 车辆的电池容量
    public Car(boolean isFastCharge, int queueSeq, double requestedChargingCapacity, double carBatteryCapacity) {
        this.isFastCharge = isFastCharge;
        QueueSeq = queueSeq;
        PrimaryKey = nextPrimaryKey;
        RequestedChargingCapacity = requestedChargingCapacity;
        CarBatteryCapacity = carBatteryCapacity;
        nextPrimaryKey ++;
    }
    public Car(boolean isFastCharge, int queueSeq, double requestedChargingCapacity, double carBatteryCapacity, int primaryKey) {
        this.isFastCharge = isFastCharge;
        QueueSeq = queueSeq;
        PrimaryKey = primaryKey;
        RequestedChargingCapacity = requestedChargingCapacity;
        CarBatteryCapacity = carBatteryCapacity;
    }
    public double getCarBatteryCapacity() {
        return CarBatteryCapacity;
    }

    public double getRequestedChargingCapacity() {
        return RequestedChargingCapacity;
    }

    public void setRequestedChargingCapacity(double requestedChargingCapacity) {
        RequestedChargingCapacity = requestedChargingCapacity;
    }


    public boolean isFastCharging() {
        return isFastCharge;
    }

    public void setChargingMode(boolean isFastCharge) {
        this.isFastCharge = isFastCharge;
    }

    public int getQueueSeq() {
        return QueueSeq;
    }

    public void setQueueSeq(int queueSeq) {
        QueueSeq = queueSeq;
    }

    public int getPrimaryKey() {
        return PrimaryKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return car.PrimaryKey == this.PrimaryKey;
    }

    @Override
    public int compareTo(Car o) {
        if (o == null) {
            throw new NullPointerException("Don't Give Me a NULL Car.Car!");
        }
        if (o.getClass() == this.getClass()) {
            return this.QueueSeq - o.QueueSeq;
        }
        throw new IllegalArgumentException("Don't Give Me something that is NOT a Car.Car!");
    }
    public Car getDeepCopy() {
       return new Car(this.isFastCharge, this.getQueueSeq(), this.getRequestedChargingCapacity(), this.getCarBatteryCapacity(), this.PrimaryKey);
    }
}
