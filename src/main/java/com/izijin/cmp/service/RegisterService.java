package com.izijin.cmp.service;

import com.izijin.cmp.mapper.RegisterMapper;
import com.izijin.cmp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class RegisterService {

    private final PasswordEncoder passwordEncoder; // 确保这行存在

    @Autowired
    public RegisterService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private RegisterMapper registerMapper;

    @Transactional
    public User registerUser(User user) {
        System.out.println("开始注册用户: " + user);

        // 验证手机号是否已存在
        if (registerMapper.existsByTelephone(user.getTelephone())) {
            System.out.println("手机号已存在: " + user.getTelephone());
            throw new IllegalArgumentException("该手机号已被注册");
        }

        // 验证用户名是否已存在
        if (registerMapper.existsByUsername(user.getUsername())) {
            System.out.println("用户名已存在: " + user.getUsername());
            throw new IllegalArgumentException("用户名已存在");
        }

        // 验证验证码
        if (!validateVerificationCode(user.getTelephone(), user.getVerificationCode())) {
            System.out.println("验证码无效: " + user.getVerificationCode());
            throw new IllegalArgumentException("验证码无效或已过期");
        }

        // 数据验证
        validateUserData(user);

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认值
        setDefaultValues(user);

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // 插入用户
        System.out.println("插入用户数据: " + user);
        registerMapper.insertUser(user);

        // 清除敏感信息
        user.setPassword(null); // 清除密码
        user.setVerificationCode(null); // 清除验证码   

        System.out.println("用户注册成功: " + user);
        return user;
    }

    private boolean validateVerificationCode(String telephone, String verificationCode) {
        // 这里应该实现验证码的验证逻辑
        // 暂时返回true，实际应该查询数据库或缓存来验证
        return true;
    }

    public boolean existsByUsername(String username) {
        return registerMapper.existsByUsername(username);
    }

    private void validateUserData(User user) {
        System.out.println("开始验证用户数据");

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            System.out.println("用户名为空");
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (user.getUsername().length() < 3) {
            System.out.println("用户名长度不足");
            throw new IllegalArgumentException("用户名长度不能少于3个字符");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            System.out.println("密码长度不足");
            throw new IllegalArgumentException("密码不能少于6个字符");
        }
        if (user.getTelephone() == null || !user.getTelephone().matches("^1[3-9]\\d{9}$")) {
            System.out.println("无效的手机号码: " + user.getTelephone());
            throw new IllegalArgumentException("无效的手机号码");
        }
        if (user.getVerificationCode() == null || user.getVerificationCode().length() != 6) {
            System.out.println("无效的验证码: " + user.getVerificationCode());
            throw new IllegalArgumentException("验证码必须是6位数字");
        }

        System.out.println("用户数据验证通过");
    }

    private void setDefaultValues(User user) {
        if (user.getUserType() == null) {
            user.setUserType(1); // 默认为普通用户
        }
        if (user.getAvatar() == null) {
            user.setAvatar("personalheadlike.png"); // 设置默认头像
        }
        if (user.getNickname() == null) {
            user.setNickname(user.getUsername()); // 设置默认昵称为用户名
        }
    }
}
