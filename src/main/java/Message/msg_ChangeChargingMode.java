package Message;

import Car.Car;

import java.util.ArrayList;
import java.util.List;

public class msg_ChangeChargingMode extends Message{
    public Car car;

    public msg_ChangeChargingMode(Car car) {
        super("Change_Charging_Mode");
        this.car = car;
    }

    public static void main(String[] args) {
        List<Car> cars = new ArrayList<>();
        cars.add(new Car(false,23,45,67,8));
        System.out.println(cars);
        cars.add(new Car(true,23,45,67,8));
        System.out.println(cars);
        cars.remove(new Car(true,23,45,67,8));
        System.out.println(cars);
        cars.remove(new Car(true,23,45,67,8));
        System.out.println(cars);
    }
}
