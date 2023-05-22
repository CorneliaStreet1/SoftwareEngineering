package Message;

import Car.Car;

public class msg_CheckSequenceNum extends Message{
    public Car car;
    public msg_CheckSequenceNum(Car c) {
        super("Check_Sequence_Num");
        car = c;
    }
}
