package com.izijin.cmp.service;

import com.izijin.cmp.mapper.RegisterMapper;
import com.izijin.cmp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterService {

    @Autowired
    private RegisterMapper registerMapper;

    @Transactional
    public User registerUser(User user) {
        if (registerMapper.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 数据验证
        validateUserData(user);

        // 设置默认值
        setDefaultValues(user);

        // 插入用户
        registerMapper.insertUser(user);

        // 清除敏感信息
        user.setPassword(null);

        return user;
    }

    public boolean existsByUsername(String username) {
        return registerMapper.existsByUsername(username);
    }

    private void validateUserData(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("密码不能少于6个字符");
        }
        // 可以添加更多的验证逻辑，如邮箱格式、手机号格式等
    }

    private void setDefaultValues(User user) {
        if (user.getUserType() == null) {
            user.setUserType(1); // 默认为普通用户
        }
        if (user.getAvatar() == null) {
            user.setAvatar("default_avatar.png"); // 设置默认头像
        }
    }
}
