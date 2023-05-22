package Message;

public class msg_UserLogin extends Message{
    public String UserName;
    public String UserPassword;

    public msg_UserLogin(String userName, String userPassword) {
        super("User_Login");
        UserName = userName;
        UserPassword = userPassword;
    }
}
