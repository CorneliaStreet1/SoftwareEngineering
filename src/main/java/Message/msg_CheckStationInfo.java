package Message;

import java.util.concurrent.CompletableFuture;

public class msg_CheckStationInfo extends Message {
    public int StationIndex;
    public msg_CheckStationInfo(int stationIndex, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Check_Station_Info", CompletableFuture_result_Json);
        StationIndex = stationIndex;
    }
}
