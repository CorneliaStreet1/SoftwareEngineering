package Message;

import java.util.concurrent.CompletableFuture;

public class msg_CheckAllStationState extends Message{
    public msg_CheckAllStationState(CompletableFuture<String> CompletableFuture_result_Json) {
        super("Check_All_Station_State", CompletableFuture_result_Json);
    }
}
