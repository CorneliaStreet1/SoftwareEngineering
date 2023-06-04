package Message;

import java.util.concurrent.CompletableFuture;

public class msg_ShowStationTable extends Message{
    public msg_ShowStationTable(CompletableFuture<String> CompletableFuture_result_Json) {
        super("Show_Station_Table", CompletableFuture_result_Json);
    }
}
