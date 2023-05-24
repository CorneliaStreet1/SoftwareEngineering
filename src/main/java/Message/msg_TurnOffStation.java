package Message;

import java.util.concurrent.CompletableFuture;

public class msg_TurnOffStation extends Message{
    public int StationIndex;
    public msg_TurnOffStation(int stationIndex, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Turn_Off_Station", CompletableFuture_result_Json);
        StationIndex = stationIndex;
    }
}
