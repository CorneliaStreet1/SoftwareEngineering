package Message;

import java.util.concurrent.CompletableFuture;

public class Message {
    public final String Type;
    public CompletableFuture<String> Result_Json;
    public Message(String type, CompletableFuture<String> CompletableFuture_result_Json) {
        Result_Json = CompletableFuture_result_Json;
        Type = type;
    }
}
