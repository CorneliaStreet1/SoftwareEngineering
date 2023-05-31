package Message;

import Car.Car;

import java.util.concurrent.CompletableFuture;

public class msg_CheckForwardCarAmount extends Message{
    public Car car;

    public msg_CheckForwardCarAmount(Car car, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Check_Forward_CarAmount", CompletableFuture_result_Json);
        this.car = car;
    }
}
