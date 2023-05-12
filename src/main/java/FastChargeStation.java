public class FastChargeStation extends ChargeStation{
    public static final int ChargingSpeed = 30;
    public FastChargeStation() {
        super();
    }

    public synchronized boolean JoinFastStation(Car car) {
        if (car.isFastCharging()) {
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
