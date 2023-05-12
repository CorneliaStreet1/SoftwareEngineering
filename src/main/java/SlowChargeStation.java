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
        return time;
    }
}
