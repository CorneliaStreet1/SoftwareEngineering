package Message;

import java.util.concurrent.CompletableFuture;

public class msg_ChargeComplete extends Message{
    public int StationIndex;
    public boolean isFast;
    public msg_ChargeComplete(CompletableFuture<String> CompletableFuture_result_Json, int stationIndex, boolean isfast) {
        super("Charging_Complete", CompletableFuture_result_Json);
        StationIndex = stationIndex;
        this.isFast = isfast;
    }
}
