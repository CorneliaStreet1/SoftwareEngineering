package UserManagement;

public class UserManager {
    public static boolean  UserRegistration(String usrName, String psw) {
        User user = new User(usrName, psw);
        //TODO：将用户信息写入数据库（完成StoreUser()方法）
        return true;
    }
    private static void StoreUser(User user) {
        //TODO:将用户信息写入持久化层
        return;
    }
    public static boolean UserLogIn(String usrName, String psw) {
        if (psw == null) {
            return false;
        }
        String Expected_PSW = FindPasswordByUsrName(usrName);
        return psw.equals(Expected_PSW);
    }
    private static String FindPasswordByUsrName(String usrName) {
        return null;
    }
}
