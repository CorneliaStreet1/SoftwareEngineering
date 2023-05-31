package Message;

import java.util.concurrent.CompletableFuture;

public class msg_StationFault extends Message {
    public int StationIndex;
    public int SchedulingStrategy;
    public msg_StationFault(int stationIndex, int schedulingStrategy, CompletableFuture<String> CompletableFuture_result_Json) {
        super("Station_Fault", CompletableFuture_result_Json);
        StationIndex = stationIndex;
        SchedulingStrategy = schedulingStrategy;
    }
}
