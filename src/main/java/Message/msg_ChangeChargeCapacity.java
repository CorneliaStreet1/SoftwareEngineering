package Message;

import Car.Car;

import java.util.concurrent.CompletableFuture;

public class msg_ChangeChargeCapacity extends Message{
    public double NewValue;
    public Car car;
    public msg_ChangeChargeCapacity(double newValue, Car aCar, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Change_Charge_Capacity", CompletableFuture_result_Json);
        NewValue = newValue;
        car = aCar;
    }
}
