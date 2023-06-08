package UserManagement;

import mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojo.User;

public class UserManager {
    private static Logger logger = LogManager.getLogger(UserManager.class);
    private static SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtils.getSqlSessionFactory();

    public static boolean  UserRegistration(String usrName, String psw, boolean isAdmin) {
        //TODO 需要先检查用户名是否重复，重复的话直接返回false
        if (FindUserInfoByUsrName(usrName) != null) {
            logger.info("!!!!!Registration FAILED, Duplicate Username: " + usrName);
            return false;
        }else {
            User user = new User(usrName, psw, isAdmin);
            //TODO：将用户信息写入数据库（完成StoreUser()方法）
            StoreUser(user);
            return true;
        }
    }
    private static void StoreUser(User user) {
        logger.info("START====Store User " + user.getUserName());
        //TODO:将用户信息写入持久化层
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.add(user);
        sqlSession.commit();
        sqlSession.close();
        logger.info("END====Store User " + user.getUserName());
        return;
    }
    public static LoginResult UserLogIn(String usrName, String psw) {
        logger.info("START=========UserLogIn");
        if (psw == null || usrName == null) {
            logger.info("Null user name or NULL psw");
            return new LoginResult(false, false, -1);
        }
        logger.info("User: " + usrName);
        User Expected_Usr = FindUserInfoByUsrName(usrName);
        LoginResult loginResult = new LoginResult();
        if (Expected_Usr != null) {
            logger.info("User Found in DB: " + Expected_Usr.getUserName() + " isAdmin: " + Expected_Usr.getIsAdmin());
            if (psw.equals(Expected_Usr.getPassWord())) {
                logger.info("Login Success");
                loginResult.Login_Success = true;
                loginResult.isAdmin = Expected_Usr.getIsAdmin();
                loginResult.User_ID = Expected_Usr.getUID();
            }
            else {
                logger.info("!!!!Login FAILED: Wrong psw");
                loginResult.Login_Success = false;
                loginResult.User_ID = -1;
                loginResult.isAdmin = false;
            }
        }else {
            logger.info("!!!!Login FAILED: User NOT FOUND: " + usrName);
            loginResult.Login_Success = false;
            loginResult.User_ID = -1;
            loginResult.isAdmin = false;
        }
        logger.info("END=========UserLogIn");
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
