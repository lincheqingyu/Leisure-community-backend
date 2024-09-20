package com.izijin.cmp.mapper;

import com.izijin.cmp.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface RegisterMapper {

    @Insert("INSERT INTO users (username, password, nickname, email, telephone, userType, school, avatar, authenticationInfo, verification_code, created_at, updated_at) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{email}, #{telephone}, #{userType}, #{school}, #{avatar}, #{authenticationInfo}, #{verificationCode}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(String username);

    // 可能需要的其他方法
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM users WHERE telephone = #{telephone}")
    User findByTelephone(String telephone);

    @Update("UPDATE users SET avatar = #{avatar}, nickname = #{nickname}, email = #{email}, " +
            "telephone = #{telephone}, password = #{password}, userType = #{userType}, " +
            "school = #{school}, authenticationInfo = #{authenticationInfo}, " +
            "verification_code = #{verificationCode}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    void updateUser(User user);

    @Select("SELECT COUNT(*) > 0 FROM users WHERE telephone = #{telephone}")
    boolean existsByTelephone(String telephone);
}
