import Car.Car;
import Message.msg_CancelCharging;
import Message.msg_ChangeChargeCapacity;
import Message.msg_ChangeChargingMode;
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
    public void Test_CancelCharge() {
        boolean flag = false;//区分两种不同的测试情况。一种是有等候区的车取消。另外一种是没有。
        try {
            System.out.println("Hello");
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
                //用一个线程模拟取消正在充电的车的充电
                Thread thread1 = new Thread(()-> {
                    try {
                        System.out.println("+++++++++++++++++++++Thread1");
                        CompletableFuture<String> objectCompletableFuture = new CompletableFuture<>();
                        Server.MessageQueue.put(new msg_CancelCharging(new Car(0), objectCompletableFuture));
                        String s = objectCompletableFuture.get();
                        System.out.println(Thread.currentThread().getName() + "**************Result" + s);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, "Thread1");

                //用另外一个线程模拟取消在充电桩等候充电的车的充电
                Thread thread2 = new Thread(() -> {
                    try {
                        System.out.println("+++++++++++++++++++Thread2");
                        CompletableFuture<String> stringCompletableFuture = new CompletableFuture<>();
                        Server.MessageQueue.put(new msg_CancelCharging(new Car(3), stringCompletableFuture));
                        String s1 = stringCompletableFuture.get();
                        System.out.println(Thread.currentThread().getName() + "*****************Result:" + s1);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, "Thread2");
                thread1.start();
                thread2.start();
                server.run();
            }else {
                //取消等候区的车
                for (int i = 0; i < 9; i++) {
                    /*
                     * 恰好每个充电桩都能分配到两辆车。
                     * 这种情况是上一个测试在i < 6的情况下测过了、详情见2023-06-05-20-29-47.log
                     * */
                    Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(true, 0.05, 0.5, i), new CompletableFuture<>()));
                    Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(false, 0.01166667, 0.12, i + 9), new CompletableFuture<>()));
                }
                //用一个线程模拟取消正在充电的车的充电
                Thread thread1 = new Thread(()-> {
                    try {
                        System.out.println("+++++++++++++++++++++Thread1");
                        CompletableFuture<String> objectCompletableFuture = new CompletableFuture<>();
                        Server.MessageQueue.put(new msg_CancelCharging(new Car(0), objectCompletableFuture));
                        String s = objectCompletableFuture.get();
                        System.out.println(Thread.currentThread().getName() + "**************Result" + s);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, "Thread1");

                //用另外一个线程模拟取消在充电桩等候充电的车的充电
                Thread thread2 = new Thread(() -> {
                    try {
                        System.out.println("+++++++++++++++++++Thread2");
                        CompletableFuture<String> stringCompletableFuture = new CompletableFuture<>();
                        Server.MessageQueue.put(new msg_CancelCharging(new Car(3), stringCompletableFuture));
                        String s1 = stringCompletableFuture.get();
                        System.out.println(Thread.currentThread().getName() + "*****************Result:" + s1);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, "Thread2");
                //取消等候区的车的充电
                Thread thread3 = new Thread(() -> {
                    try {
                        System.out.println("+++++++++++++++++++Thread3");
                        CompletableFuture<String> stringCompletableFuture = new CompletableFuture<>();
                        Server.MessageQueue.put(new msg_CancelCharging(new Car(17), stringCompletableFuture));
                        String s1 = stringCompletableFuture.get();
                        System.out.println(Thread.currentThread().getName() + "*****************Result:" + s1);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, "Thread3");
                thread1.start();
                thread2.start();
                thread3.start();
                server.run();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void TestCancel_Light() {
        try {
            /*
            * Debug用的
            * 上面那个测试发现了Bug
            * */
            System.out.println("Hello");
            Server server = new Server(1, 0);
            Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(true, 0.05, 0.5, 0), new CompletableFuture<>()));
            Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(true, 0.05, 0.5, 1), new CompletableFuture<>()));
            CompletableFuture<String> objectCompletableFuture = new CompletableFuture<>();
            Server.MessageQueue.put(new msg_CancelCharging(new Car(0), objectCompletableFuture));
            CompletableFuture<String> stringCompletableFuture = new CompletableFuture<>();
            Server.MessageQueue.put(new msg_CancelCharging(new Car(1), stringCompletableFuture));
            server.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    /*
    * 改变充电模式
    * 测试如下几种情况：
    * 等待区改变充电模式，重新排队
    * 充电区改变充电模式，不允许
    * 上述两种情况同时发生
    * */
    @Test
    public void Test_ChangeCharging_Mode() {
        /*
        * 一个快桩
        * 一个慢桩
        * 车辆：2+2+6
        * 2023-06-07-02-38-57.log
        * */
        Server server = new Server(1, 1);
        try {
            for (int i = 0; i < 5; i++) {
                /*
                 * 快车:0 1 2 3 4
                 * 慢车:5 6 7 8 9
                 * */
                Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(true, 0.05, 0.05, i), new CompletableFuture<>()));
                Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(false, 0.01166667, 0.05, i + 5), new CompletableFuture<>()));
            }
            /*
            * 简单模拟。就不用多线程加入改变充电模式的消息了
            * 处理完所有进入等待区的消息后：
            * 快充桩:0 1 快等待:2 3 4
            * 慢充桩:5 6 慢等待:7 8 9
            *
            * 处理完所有改变充电模式的消息后：
             * 快充桩:0 1 快等待:2 3 9
             * 慢充桩:5 6 慢等待:7 8 4
             * 改变之后，9号和4号车的ETA从大约6秒，分别变为1.4秒和25秒
            * */
            //改变0号和6号的充电模式。预期请求被拒绝
            Server.MessageQueue.put(new msg_ChangeChargingMode(new Car(true, 0), new CompletableFuture<>()));
            Server.MessageQueue.put(new msg_ChangeChargingMode(new Car(false,  6), new CompletableFuture<>()));
            //改变4和9号的。预取请求成功
            Server.MessageQueue.put(new msg_ChangeChargingMode(new Car(true, 4), new CompletableFuture<>()));
            Server.MessageQueue.put(new msg_ChangeChargingMode(new Car(false, 9), new CompletableFuture<>()));
            server.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    /*
    * 改变充电电量
    * 测试如下几种情况：
    * 等候区改变充电电量，排队位置不变
    * 充电区改变充电电量，不允许
    * 上述两种情况同时发生
    * */
    @Test
    public void Test_ChangeChargingCapacity() {
        /*
         * 一个快桩
         * 一个慢桩
         * 车辆：2+2+6
         * 2023-06-07-03-15-58.log
         * */
        Server server = new Server(1, 1);
        try {
            for (int i = 0; i < 5; i++) {
                /*
                 * 快车:0 1 2 3 4
                 * 慢车:5 6 7 8 9
                 * */
                Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(true, 0.05, 0.05, i), new CompletableFuture<>()));
                Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(false, 0.01166667, 0.05, i + 5), new CompletableFuture<>()));
            }
            /*
            * 简单模拟。就不用多线程加入改变充电模式的消息了
            * 处理完所有进入等待区的消息后：
            * 快充桩:0 1 快等待:2 3 4
            * 慢充桩:5 6 慢等待:7 8 9
            **/
            //改变0号和6号的充电电量。预期请求被拒绝
            Server.MessageQueue.put(new msg_ChangeChargeCapacity(0.05 * 2, new Car(0), new CompletableFuture<>()));
            Server.MessageQueue.put(new msg_ChangeChargeCapacity(0.01166667 * 2, new Car(6), new CompletableFuture<>()));
            //改变4和9号的充电电量。加倍。ETA分别变为12秒
            Server.MessageQueue.put(new msg_ChangeChargeCapacity(0.05 * 2, new Car(4), new CompletableFuture<>()));
            Server.MessageQueue.put(new msg_ChangeChargeCapacity(0.01166667 * 2, new Car(9), new CompletableFuture<>()));
            server.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}