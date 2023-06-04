package Message;

import java.util.concurrent.CompletableFuture;

public class msg_Authentication extends Message {
    public int UserID;
    public msg_Authentication(CompletableFuture<String> CompletableFuture_result_Json, int UID) {
        super("Authentication", CompletableFuture_result_Json);
        UserID = UID;
    }
}
