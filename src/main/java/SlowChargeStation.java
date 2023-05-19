public class SlowChargeStation extends ChargeStation{
    public static final int ChargingSpeed = 7;
    public SlowChargeStation() {
        super();
    }

    public synchronized boolean JoinSlowStation(Car car) {
        if (!car.isFastCharging()) {
            return super.JoinStation(car);
        }
        return false;
    }
    public synchronized double getWaitingTime() {
        double time = 0;
        for (Car car : super.getCarQueue()) {
            time += car.getChargingCapacity() / ChargingSpeed;
        }
        //TODO：目前这个方法使用每辆车的充电容量除以充电速度来估计充电时间。但是对于正在充电的车，应该用其剩余充电容量（比如要冲100度，已经充了80度了，应该用20除以30，而不是1000/30）
        return time;
    }
}
