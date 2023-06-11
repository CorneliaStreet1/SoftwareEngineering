package Message;

import Car.Car;

import java.util.concurrent.CompletableFuture;

public class msg_EnterWaitingZone extends Message{
    public Car UserCar; //用户要加入等候区的车子
    public msg_EnterWaitingZone(Car car, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Enter_Waiting_Zone", CompletableFuture_result_Json);
        UserCar = car;
    }

    public static void main(String[] args) {
/*        Gson gson = new Gson();
        msg_EnterWaitingZone msg = new msg_EnterWaitingZone(new Car(true, -1, 114, 514, -2));
        String js = gson.toJson(msg, msg.getClass());
        System.out.println(js);
        Message P = gson.fromJson(js, Message.class);
        System.out.println(P.Type);
        msg_EnterWaitingZone ms = gson.fromJson(js, msg_EnterWaitingZone.class);
        System.out.println(ms.UserCar);*/
        /* 不能直接将P强制类型转换成它的子类然后访问子类独有的字段
        msg_EnterWaitingZone s = (msg_EnterWaitingZone) P;
        System.out.println(s.UserCar);
        */
    }
}
