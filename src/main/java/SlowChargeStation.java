public class SlowChargeStation extends ChargeStation{
    public static final int ChargingSpeed = 7;
    public SlowChargeStation() {
        super();
    }

    public boolean JoinSlowStation(Car car) {
        if (!car.isFastCharging()) {
            return super.JoinStation(car);
        }
        return false;
    }

}
