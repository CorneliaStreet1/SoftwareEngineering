package Message;

import Car.Car;

public class msg_CheckChargingForm extends Message{
    public Car car;
    public msg_CheckChargingForm(Car car) {
        super("Check_Charging_Form");
        this.car = car;
    }
}
