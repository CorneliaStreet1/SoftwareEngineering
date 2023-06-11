import ChargeStation.ChargingRecord;
import UserManagement.SqlSessionFactoryUtils;
import mapper.RecordMapper;
import mapper.UserMapper;
import org.apache.ibatis.annotations.Mapper;
import org.junit.Test;
import pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
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
        String userName = "fan3";

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
        User user = new User("fan3","123456",true);

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

    @Test
    public void testSelectAll2() throws IOException {

        //1. 加载mybatis的核心配置文件，获取 SqlSessionFactory
        /*String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);*/

        //2. 获取SqlSession对象，用它来执行sql
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //3. 获取mapper接口的代理对象
        RecordMapper recordMapper = sqlSession.getMapper(RecordMapper.class);

        //4.执行方法
        List<ChargingRecord> chargingRecords = recordMapper.selectALLRecord();
        System.out.println(chargingRecords);

        //5. 释放资源
        sqlSession.close();

    }

    @Test
    public void testSelectByUserIdRecord() throws IOException {

        //模拟接受参数
        int userId = 2;

        //1. 加载mybatis的核心配置文件，获取 SqlSessionFactory
        /*String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);*/

        //2. 获取SqlSession对象，用它来执行sql
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //3. 获取mapper接口的代理对象
        RecordMapper recordMapper = sqlSession.getMapper(RecordMapper.class);

        //4.执行方法
        List<ChargingRecord> chargingRecords = recordMapper.selectByUserId(userId);
        System.out.println(chargingRecords);

        //5. 释放资源
        sqlSession.close();



    }

    @Test
    public void testSelectByUserNameRecord() throws IOException {

        //模拟接受参数
        String userName = "user2";

        //1. 加载mybatis的核心配置文件，获取 SqlSessionFactory
        /*String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);*/

        //2. 获取SqlSession对象，用它来执行sql
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //3. 获取mapper接口的代理对象
        RecordMapper recordMapper = sqlSession.getMapper(RecordMapper.class);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        //4.执行方法
        User user = userMapper.selectByUserName(userName);
        List<ChargingRecord> chargingRecords = recordMapper.selectByUserId(user.getUID());
        System.out.println(chargingRecords);

        //5. 释放资源
        sqlSession.close();


    }

    @Test
    public void testAddRecord() throws IOException {

        //模拟接受参数
        String s1 = "2023-06-07T18:46:04.047";
        String s2 = "2023-06-08T05:32:02.049";
        LocalDateTime localDateTime = LocalDateTime.now();
        String s0 = localDateTime.toString();

        ChargingRecord chargingRecord = new ChargingRecord("2023-06-07T18:46:04.0489",1,s0,3,
                32.32,s1,s2,23.32,2.3);

        //1. 加载mybatis的核心配置文件，获取 SqlSessionFactory
        /*String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);*/

        //2. 获取SqlSession对象，用它来执行sql
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //3. 获取mapper接口的代理对象
        RecordMapper recordMapper = sqlSession.getMapper(RecordMapper.class);

        //4.执行方法
        recordMapper.add(chargingRecord);

        //提交事务
        sqlSession.commit();

        //5. 释放资源
        sqlSession.close();

    }



}
