public class FastChargeStation extends ChargeStation{
    public static final int ChargingSpeed = 30;
    public FastChargeStation() {
        super();
    }

    public boolean JoinFastStation(Car car) {
        if (car.isFastCharging()) {
            return super.JoinStation(car);
        }
        return false;
    }
}
