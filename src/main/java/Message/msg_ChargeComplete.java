package Message;

import java.util.concurrent.CompletableFuture;

public class msg_ChargeComplete extends Message{

    public msg_ChargeComplete(CompletableFuture<String> CompletableFuture_result_Json) {
        super("Charging_Complete", CompletableFuture_result_Json);
    }
}
