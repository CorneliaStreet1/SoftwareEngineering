import UserManagement.SqlSessionFactoryUtils;
import mapper.UserMapper;
import org.junit.Test;
import pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Mybatis 快速入门代码
 */
public class MybatisTest {
    private static SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtils.getSqlSessionFactory();


    @Test
    public void testSelectAll() throws IOException {

        //1. 加载mybatis的核心配置文件，获取 SqlSessionFactory
        /*String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);*/

        //2. 获取SqlSession对象，用它来执行sql
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //3. 获取mapper接口的代理对象
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        //4.执行方法
        List<User> users = userMapper.selectALL();
        System.out.println(users);

        //5. 释放资源
        sqlSession.close();

    }


    @Test
    public void testSelectByUserName() throws IOException {

        //模拟接受参数
        String userName = "fan2";

        //1. 加载mybatis的核心配置文件，获取 SqlSessionFactory
        /*String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);*/

        //2. 获取SqlSession对象，用它来执行sql
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //3. 获取mapper接口的代理对象
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        //4.执行方法
        User user = userMapper.selectByUserName(userName);
        System.out.println(user);

        //5. 释放资源
        sqlSession.close();

    }


    @Test
    public void testSelectByUserId() throws IOException {

        //模拟接受参数
        int userId = 2;

        //1. 加载mybatis的核心配置文件，获取 SqlSessionFactory
        /*String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);*/

        //2. 获取SqlSession对象，用它来执行sql
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //3. 获取mapper接口的代理对象
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        //4.执行方法
        User user = userMapper.selectByUserId(userId);
        System.out.println(user);

        //5. 释放资源
        sqlSession.close();

    }




    @Test
    public void testAdd() throws IOException {

        //模拟接受参数
        User user = new User("fan8","123456",true);

        //1. 加载mybatis的核心配置文件，获取 SqlSessionFactory
        /*String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);*/

        //2. 获取SqlSession对象，用它来执行sql
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //3. 获取mapper接口的代理对象
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        //4.执行方法
        userMapper.add(user);
        //System.out.println(user);

        //提交事务
        sqlSession.commit();

        //5. 释放资源
        sqlSession.close();

    }



}
