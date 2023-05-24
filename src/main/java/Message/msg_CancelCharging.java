package Message;

import Car.Car;

import java.util.concurrent.CompletableFuture;

public class msg_CancelCharging extends Message{
    public Car UserCar;
    public msg_CancelCharging(Car car, CompletableFuture<String> Result_Json) {
        super("Cancel_Charging", Result_Json);
        UserCar = car;
    }

    public static void main(String[] args) {

    }
}
