package Message;

import java.util.concurrent.CompletableFuture;

public class msg_UserRegistration extends Message{
    public String UserName;
    public String UserPassword;
    public boolean isAdmin;
    public msg_UserRegistration(String userName, String userPassword, CompletableFuture<String> CompletableFuture_result_Json, boolean isAdmin) {
        super("User_Registration", CompletableFuture_result_Json);
        UserName = userName;
        UserPassword = userPassword;
        this.isAdmin = isAdmin;
    }
}
