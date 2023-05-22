package Message;

import Car.Car;

public class msg_CheckForwardCarAmount extends Message{
    public Car car;

    public msg_CheckForwardCarAmount(Car car) {
        super("Check_Forward_CarAmount");
        this.car = car;
    }
}
