package com.izijin.cmp.mapper;

import com.izijin.cmp.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users")
    List<User> findAllUsers();
    
    @Select("SELECT * FROM users WHERE username = #{username} AND password = #{password}")
    User find(@Param("username") String username, @Param("password") String password);

    @Select("SELECT * FROM users WHERE user_type = 2 LIMIT #{limit} OFFSET #{offset}")
    List<User> findAllStudents(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO users (avatar, nickname, username, telephone, password, email, user_type, " +
            "verification_code, created_at, updated_at, level, followers, following, likes, location, school, authenticationInfo) " +
            "VALUES (#{avatar}, #{nickname}, #{username}, #{telephone}, #{password}, #{email}, #{userType}, " +
            "#{verificationCode}, #{createdAt}, #{updatedAt}, #{level}, #{followers}, #{following}, #{likes}, #{location}, #{school}, #{authenticationInfo})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(String username);

    @Select("SELECT id, avatar, nickname, username, level, location, " +
            "followers, following, likes, school, authenticationInfo " +
            "FROM users WHERE id = #{id} AND user_type = 1")
    User getStudentInfo(@Param("id") Integer id);

    @Update("UPDATE users SET avatar = #{avatar}, nickname = #{nickname}, telephone = #{telephone}, " +
            "email = #{email}, verification_code = #{verificationCode}, updated_at = #{updatedAt}, " +
            "level = #{level}, followers = #{followers}, following = #{following}, likes = #{likes}, " +
            "location = #{location}, school = #{school}, authenticationInfo = #{authenticationInfo}, " +
            "password = #{password} " +  // 添加这一行来更新密码
            "WHERE id = #{id}")
    void updateUser(User user);

    @Delete("DELETE FROM users WHERE id = #{id}")
    void deleteUser(@Param("id") Integer id);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Integer id);
}