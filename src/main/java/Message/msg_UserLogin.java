package Message;

import java.util.concurrent.CompletableFuture;

public class msg_UserLogin extends Message{
    public String UserName;
    public String UserPassword;

    public msg_UserLogin(String userName, String userPassword, CompletableFuture<String> CompletableFuture_result_Json) {
        super("User_Login", CompletableFuture_result_Json);
        UserName = userName;
        UserPassword = userPassword;
    }
}
