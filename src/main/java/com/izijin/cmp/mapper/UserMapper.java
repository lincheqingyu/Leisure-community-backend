package com.izijin.cmp.mapper;

import com.izijin.cmp.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User find(@Param("username") String username, @Param("password") String password); // 根据用户名和密码查找用户
    List<User> findAllStudents(); // 查找所有学生
    User findByUsername(String username); // 根据用户名查找用户
    
    void insertUser(User user); // 插入用户
    boolean existsByUsername(String username); // 检查用户是否存在
}
