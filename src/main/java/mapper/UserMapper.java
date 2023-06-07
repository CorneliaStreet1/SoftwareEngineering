package mapper;

import pojo.User;

import java.util.List;


public interface UserMapper {

    //查看所有user
    List<User> selectALL();

    //根据username查询
    User selectByUserName(String userName);

    //根据userid查询
    User selectByUserId(int userId);

    //添加user
    void add(User user);
}
