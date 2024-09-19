package com.izijin.cmp.model;

import lombok.Data;
import java.time.LocalDateTime;
import javax.persistence.Column;

@Data
public class User {
    private Integer id;
    private String avatar;
    private String nickname;
    private String username;
    private String telephone;
    private String password;
    private String email;
    private Integer userType;
    private String school;
    private String authenticationInfo;
    @Column(name = "verification_code")
    private String verificationCode;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 如果需要，可以添加构造函数
    public User() {}

    // 可以添加一个包含所有字段的构造函数
    public User(Integer id, String avatar, String nickname, String username, String telephone,
                String password, String email, Integer userType, String school,
                String authenticationInfo, String verificationCode,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;                                     // 用户ID
        this.avatar = avatar;                             // 头像
        this.nickname = nickname;                         // 昵称
        this.username = username;                         // 用户名
        this.telephone = telephone;                       // 电话号码
        this.password = password;                         // 密码
        this.email = email;                               // 邮箱
        this.userType = userType;                         // 用户类型
        this.school = school;                             // 学校
        this.authenticationInfo = authenticationInfo;     // 认证信息
        this.verificationCode = verificationCode;         // 验证码
        this.createdAt = createdAt;                       // 创建时间
        this.updatedAt = updatedAt;                       // 更新时间
    }

    // 如果需要，可以在这里添加自定义方法
}