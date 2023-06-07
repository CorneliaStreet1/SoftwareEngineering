package UserManagement;

import mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import pojo.User;

public class UserManager {
    private static SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtils.getSqlSessionFactory();

    public static boolean  UserRegistration(String usrName, String psw, boolean isAdmin) {
        //TODO 需要先检查用户名是否重复，重复的话直接返回false
        if (FindUserInfoByUsrName(usrName) != null) {
            return false;
        }else {
            User user = new User(usrName, psw, isAdmin);
            //TODO：将用户信息写入数据库（完成StoreUser()方法）
            StoreUser(user);
            return true;
        }
    }
    private static void StoreUser(User user) {
        //TODO:将用户信息写入持久化层
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.add(user);
        sqlSession.commit();
        sqlSession.close();

        return;
    }
    public static LoginResult UserLogIn(String usrName, String psw) {
        if (psw == null || usrName == null) {
            return new LoginResult(false, false, -1);
        }
        User Expected_Usr = FindUserInfoByUsrName(usrName);
        LoginResult loginResult = new LoginResult();
        if (Expected_Usr != null) {
            if (psw.equals(Expected_Usr.getPassWord())) {
                loginResult.Login_Success = true;
                loginResult.isAdmin = Expected_Usr.getIsAdmin();
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
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.selectByUserName(usrName);
        sqlSession.close();

        return user;
    }
    public static User FindUserInfoByUsrUID(int UID) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.selectByUserId(UID);
        sqlSession.close();

        return user;

    }
}
