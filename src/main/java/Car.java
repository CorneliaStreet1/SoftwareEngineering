public class Car implements Comparable<Car>{
    private static int nextPrimaryKey = 0;
    private boolean isFastCharge; //是否是快充模式
    private int QueueSeq; //到达的序号
    private int PrimaryKey;
    private double ChargingCapacity;

    public double getChargingCapacity() {
        return ChargingCapacity;
    }

    public void setChargingCapacity(double chargingCapacity) {
        ChargingCapacity = chargingCapacity;
    }

    public Car(boolean isFastCharge, int queueSeq, double chargingCapacity) {
        this.isFastCharge = isFastCharge;
        QueueSeq = queueSeq;
        PrimaryKey = nextPrimaryKey;
        ChargingCapacity = chargingCapacity;
        nextPrimaryKey ++;
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
            throw new NullPointerException("Don't Give Me a NULL Car!");
        }
        if (o.getClass() == this.getClass()) {
            return this.QueueSeq - o.QueueSeq;
        }
        throw new IllegalArgumentException("Don't Give Me something that is NOT a Car!");
    }
}
