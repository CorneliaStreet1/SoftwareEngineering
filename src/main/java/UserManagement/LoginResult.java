package UserManagement;

public class LoginResult {
    public boolean Login_Success;
    public boolean isAdmin;
    public int User_ID;

    public LoginResult(boolean login_Success, boolean isAdmin, int uid) {
        Login_Success = login_Success;
        this.isAdmin = isAdmin;
        User_ID = uid;
    }

    public LoginResult() {
    }
}
