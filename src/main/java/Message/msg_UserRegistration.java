package Message;

import java.util.concurrent.CompletableFuture;

public class msg_UserRegistration extends Message{
    public String UserName;
    public String UserPassword;
    public msg_UserRegistration(String userName, String userPassword, CompletableFuture<String> CompletableFuture_result_Json) {
        super("User_Registration", CompletableFuture_result_Json);
        UserName = userName;
        UserPassword = userPassword;
    }
}
