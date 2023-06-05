import Car.Car;
import Message.msg_EnterWaitingZone;
import Server.Server;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class ServerTest {
    @Test
    public void TestEnterWaitingZone() {
        Server server = new Server(3, 3);
        try {
            for (int i = 0; i < 3; i++) {
                Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(true, 0.5, i), new CompletableFuture<>()));
                Server.MessageQueue.put(new msg_EnterWaitingZone(new Car(false, 0.1166667, i + 3), new CompletableFuture<>()));
            }
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        server.run();
    }
}