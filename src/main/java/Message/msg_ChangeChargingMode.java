package Message;

import Car.Car;

import java.util.concurrent.CompletableFuture;

public class msg_ChangeChargingMode extends Message{
    public Car car;

    public msg_ChangeChargingMode(Car car, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Change_Charging_Mode", CompletableFuture_result_Json);
        this.car = car;
    }

    public static void main(String[] args) {
//        List<Car> cars = new ArrayList<>();
//        cars.add(new Car(false,23,45,67,8));
//        System.out.println(cars);
//        cars.add(new Car(true,23,45,67,8));
//        System.out.println(cars);
//        cars.remove(new Car(true,23,45,67,8));
//        System.out.println(cars);
//        cars.remove(new Car(true,23,45,67,8));
//        System.out.println(cars);
    }
}
