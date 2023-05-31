package Message;

import Car.Car;

import java.util.concurrent.CompletableFuture;

public class msg_CheckChargingForm extends Message{
    public Car car;
    public msg_CheckChargingForm(Car car, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Check_Charging_Form", CompletableFuture_result_Json);
        this.car = car;
    }
}
