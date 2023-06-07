package pojo;

public class User {
    private int UID;
    private String UserName;
    private String Password;
    private boolean isAdmin;

    public User() {
    }

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

    public String getPassWord() {
        return Password;
    }

    public void setPassWord(String password) {
        Password = password;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "UID=" + UID +
                ", UserName='" + UserName + '\'' +
                ", Password='" + Password + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
