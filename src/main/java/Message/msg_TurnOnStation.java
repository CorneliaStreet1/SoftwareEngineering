package Message;

import java.util.concurrent.CompletableFuture;

public class msg_TurnOnStation extends Message{
    public int StationIndex;

    public msg_TurnOnStation(int stationIndex, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Turn_On_Station", CompletableFuture_result_Json);
        StationIndex = stationIndex;
    }
}
