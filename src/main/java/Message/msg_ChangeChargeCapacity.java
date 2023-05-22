package Message;

import Car.Car;

public class msg_ChangeChargeCapacity extends Message{
    public double NewValue;
    public Car car;
    public msg_ChangeChargeCapacity(double newValue, Car aCar) {
        super("Change_Charge_Capacity");
        NewValue = newValue;
        car = aCar;
    }
}
