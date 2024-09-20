package com.izijin.cmp.model;

import lombok.Data;
import java.time.LocalDateTime;
import javax.persistence.Column;

@Data
public class User {
    private Integer id;                  // 用户ID
    private String avatar;               // 头像
    private String nickname;             // 昵称
    private String username;             // 用户名
    private String telephone;            // 电话号码
    private String password;             // 密码
    private String email;                // 电子邮件
    private Integer userType;            // 用户类型
    
    @Column(name = "verification_code")
    private String verificationCode;     // 验证码
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;     // 创建时间
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;     // 更新时间

    private String level = "1";                              // 用户等级，默认为 "1"
    private Integer followers = 0;                           // 粉丝数，默认为 0
    private Integer following = 0;                           // 关注数，默认为 0
    private Integer likes = 0;                               // 点赞数，默认为 0
    private String location = "中国";                        // 位置，默认为 "中国"
    private String school = "南京理工大学紫金学院";           // 学校，默认为 "南京理工大学紫金学院"                    
    private String authenticationInfo = "未认证";            // 认证信息，默认为 "未认证"
    
    // 无参构造函数
    public User() {
        // 默认值已在字段声明时设置
    }

    // 全参数构造函数
    public User(Integer id, String avatar, String nickname, String username, String telephone,
                String password, String email, Integer userType, String school,
                String authenticationInfo, String verificationCode,
                LocalDateTime createdAt, LocalDateTime updatedAt,
                String level, Integer followers, Integer following, Integer likes, String location) {
        this.id = id;
        this.avatar = avatar;
        this.nickname = nickname;
        this.username = username;
        this.telephone = telephone;
        this.password = password;
        this.email = email;
        this.userType = userType;
        this.school = school != null ? school : this.school;
        this.authenticationInfo = authenticationInfo != null ? authenticationInfo : this.authenticationInfo;
        this.verificationCode = verificationCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.level = level != null ? level : this.level;
        this.followers = followers != null ? followers : this.followers;
        this.following = following != null ? following : this.following;
        this.likes = likes != null ? likes : this.likes;
        this.location = location != null ? location : this.location;
    }
}