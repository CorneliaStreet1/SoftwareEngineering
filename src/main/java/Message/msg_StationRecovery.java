package Message;

import java.util.concurrent.CompletableFuture;

public class msg_StationRecovery extends Message{
    public int StationIndex;
    public msg_StationRecovery(int stationIndex, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Station_Recovery", CompletableFuture_result_Json);
        StationIndex = stationIndex;
    }
}
