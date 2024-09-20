package com.izijin.cmp.service;

import com.izijin.cmp.mapper.UserMapper;
import com.izijin.cmp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        logger.info("UserService 初始化完成");
    }

    /**
     * 获取所有学生用户，支持分页
     * @param pageable 分页信息
     * @return 学生用户列表
     */
    public List<User> getAllStudents(Pageable pageable) {
        // 实现分页逻辑
        int offset = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();
        return userMapper.findAllStudents(offset, pageSize);
    }

    /**
     * 获取学生信息
     * @param id 学生ID
     * @return 学生用户信息
     */
    public User getUserInfo(Integer id) {
        User student = userMapper.getStudentInfo(id);
        if (student != null) {
            student.setLevel("Lv." + student.getLevel());
        }
        return student;
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回用户信息，失败返回null
     */
    public User login(String username, String password) {
        logger.info("尝试登录用户: {}", username);
        User user = userMapper.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            logger.info("用户 {} 登录成功", username);
            return user;
        }
        logger.warn("用户 {} 登录失败", username);
        return null;
    }

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 注册新用户
     * @param user 用户信息
     * @return 注册成功的用户信息
     */
    @Transactional
    public User registerUser(User user) {
        logger.info("尝试注册新用户: {}", user.getUsername());
        if (userMapper.existsByUsername(user.getUsername())) {
            logger.warn("注册失败，用户名已存在: {}", user.getUsername());
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insertUser(user);
        logger.info("新用户注册成功: {}", user.getUsername());
        return user;
    }

    /**
     * 更新用户信息
     * @param user 更新的用户信息
     * @return 更新后的用户信息
     */
    @Transactional
    public User updateUser(User user) {
        logger.info("尝试更新用户信息: {}", user.getUsername());
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser == null) {
            logger.warn("更新失败，用户不存在: {}", user.getUsername());
            throw new RuntimeException("用户不存在");
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateUser(user);
        logger.info("用户信息更新成功: {}", user.getUsername());
        return user;
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除成功返回true，失败返回false
     */
    public boolean deleteUser(Integer id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return false;
        }
        userMapper.deleteUser(id);
        return true;
    }

    /**
     * 修改用户密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改成功返回true，失败返回false
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        logger.info("尝试修改用户密码: {}", username);
        User user = userMapper.findByUsername(username);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateUser(user);
            logger.info("用户密码修改成功: {}", username);
            return true;
        }
        logger.warn("用户密码修改失败: {}", username);
        return false;
    }

    // 可以添加一个方法来更新现有用户的密码为加密形式
    @Transactional
    public void migrateAllPasswords() {
        List<User> users = userMapper.findAllUsers();
        for (User user : users) {
            String rawPassword = user.getPassword();         // 原始密码
            String encodedPassword = passwordEncoder.encode(rawPassword);   // 加密后的密码
            user.setPassword(encodedPassword);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateUser(user);
        }
    }
}