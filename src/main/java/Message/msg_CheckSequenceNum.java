package Message;

import Car.Car;

import java.util.concurrent.CompletableFuture;

public class msg_CheckSequenceNum extends Message{
    public Car car;
    public msg_CheckSequenceNum(Car c, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Check_Sequence_Num", CompletableFuture_result_Json);
        car = c;
    }
}
