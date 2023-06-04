package UserManagement;

public class User {
    private String UserName;
    private String Password;
    private boolean isAdmin;
    private int UID;
    public User(String userName, String password, boolean isAdmin) {
        UserName = userName;
        Password = password;
        this.isAdmin = isAdmin;
    }
    public User(String userName, String password, boolean isAdmin, int uid) {
        UserName = userName;
        Password = password;
        this.isAdmin = isAdmin;
        UID = uid;
    }
    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }
    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
    public boolean isAdmin() {
        return this.isAdmin;
    }
}
