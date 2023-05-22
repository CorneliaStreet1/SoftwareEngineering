package Message;

public class msg_UserRegistration extends Message{
    public String UserName;
    public String UserPassword;
    public msg_UserRegistration(String userName, String userPassword) {
        super("User_Registration");
        UserName = userName;
        UserPassword = userPassword;
    }
}
