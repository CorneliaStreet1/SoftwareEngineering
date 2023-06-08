package Message;

import Car.Car;

import java.util.concurrent.CompletableFuture;

public class msg_PreviewQueueSituation extends Message{
    public Car car;

    public msg_PreviewQueueSituation(Car car, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Preview_queue_situation", CompletableFuture_result_Json);
        this.car = car;
    }
}
