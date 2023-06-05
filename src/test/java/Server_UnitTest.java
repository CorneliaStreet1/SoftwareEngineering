import Car.Car;
import Message.msg_CancelCharging;
import Message.msg_EnterWaitingZone;
import Server.Server;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Server_UnitTest {
    @Test
    public void Test_EnterWaitingZone_Schedule_ChargingComplete() {
        Server server = new Server(3, 3);
        try {
            /*
            * 前18个消息对应的车可以得到充电的机会
            * 后6个消息对应的车无法进入等候区。会进入等候区失败，详情查看2023-06-05-20-44-53.log
            * */
            for (int i = 0; i < 12; i++) {
                Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(true, 0.05, 0.5,i), new CompletableFuture<>()));
                Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(false, 0.01166667, 0.12,i + 12), new CompletableFuture<>()));
            }
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        server.run();
    }
    @Test
    public void Test_() {
        boolean flag = true;//区分两种不同的测试情况。一种是有等候区的车取消。另外一种是没有。
        try {
            Server server = new Server(3, 3);
            if (flag) {
                //取消充电桩的车的充电
                for (int i = 0; i < 6; i++) {
                    /*
                     * 恰好每个充电桩都能分配到两辆车。
                     * 这种情况是上一个测试在i < 6的情况下测过了、详情见2023-06-05-20-29-47.log
                     * */
                    Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(true, 0.05, 0.5, i), new CompletableFuture<>()));
                    Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(false, 0.01166667, 0.12, i + 6), new CompletableFuture<>()));
                }
                //取消正在充电的车的充电
                CompletableFuture<String> objectCompletableFuture = new CompletableFuture<>();
                Server.MessageQueue.put(new msg_CancelCharging(new Car(0), objectCompletableFuture));
                String s = objectCompletableFuture.get();
                System.out.println("Result" + s);
                //取消在充电桩等候充电的车的充电
                CompletableFuture<String> stringCompletableFuture = new CompletableFuture<>();
                Server.MessageQueue.put(new msg_CancelCharging(new Car(3), stringCompletableFuture));
                String s1 = stringCompletableFuture.get();
                System.out.println("Result:" + s1);
            }else {

            }
            server.run();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }
}