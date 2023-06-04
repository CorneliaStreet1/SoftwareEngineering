package Message;

import java.util.concurrent.CompletableFuture;

public class msg_CheckStationInfo extends Message {
    public msg_CheckStationInfo(CompletableFuture<String> CompletableFuture_result_Json) {
        super("Check_Station_Info", CompletableFuture_result_Json);
    }
}
