package Message;

import Car.Car;

public class msg_CancelCharging extends Message{
    public Car UserCar;
    public msg_CancelCharging(Car car) {
        super("Cancel_Charging");
        UserCar = car;
    }

    public static void main(String[] args) {

    }
}
