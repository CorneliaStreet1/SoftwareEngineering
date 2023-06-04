package UserManagement;

public class UserManager {
    public static boolean  UserRegistration(String usrName, String psw, boolean isAdmin) {
        //TODO 需要先检查用户名是否重复，重复的话直接返回false
        if (FindUserInfoByUsrName(usrName) != null) {
            return false;
        }else {
            User user = new User(usrName, psw, isAdmin);
            //TODO：将用户信息写入数据库（完成StoreUser()方法）
            return true;
        }
    }
    private static void StoreUser(User user) {
        //TODO:将用户信息写入持久化层
        return;
    }
    public static LoginResult UserLogIn(String usrName, String psw) {
        if (psw == null) {
            return new LoginResult(false, false, -1);
        }
        User Expected_Usr = FindUserInfoByUsrName(usrName);
        LoginResult loginResult = new LoginResult();
        if (Expected_Usr != null) {
            if (psw.equals(Expected_Usr.getPassword())) {
                loginResult.Login_Success = true;
                loginResult.isAdmin = Expected_Usr.isAdmin();
                loginResult.User_ID = Expected_Usr.getUID();
            }
            else {
                loginResult.Login_Success = false;
                loginResult.User_ID = -1;
                loginResult.isAdmin = false;
            }
        }else {
            loginResult.Login_Success = false;
            loginResult.User_ID = -1;
            loginResult.isAdmin = false;
        }
        return loginResult;
    }
    public static User FindUserInfoByUsrName(String usrName) {
        return null;
    }
    public static User FindUserInfoByUsrUID(int UID) {
        return null;
    }
}
