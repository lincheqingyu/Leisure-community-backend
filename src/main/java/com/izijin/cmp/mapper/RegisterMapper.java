package com.izijin.cmp.mapper;

import com.izijin.cmp.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface RegisterMapper {

    @Insert("INSERT INTO users (username, password, nickname, email, telephone_number, user_type, school, avatar, authentication_info) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{email}, #{phoneNumber}, #{userType}, #{school}, #{avatar}, #{authenticationInfo})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(String username);
}
