package com.izijin.cmp.controller;

import com.izijin.cmp.mapper.UserMapper;
import com.izijin.cmp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", methods = {RequestMethod.GET, RequestMethod.POST})
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserMapper userMapper;

    @Autowired
    public LoginController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, Object> loginRequest) {
        try {
            String username = (String) loginRequest.get("username");
            String password = (String) loginRequest.get("password");

            if (username == null || password == null) {
                String response = "用户名和密码不能为空";
                logger.info("Login failed: {}, Status: {}", response, HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            User user = userMapper.findByUsername(username);
            if (user == null) {
                String response = "不存在该用户";
                logger.info("Login failed: {}, Status: {}", response, HttpStatus.NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!password.equals(user.getPassword())) {
                String response = "密码错误";
                logger.info("Login failed: {}, Status: {}", response, HttpStatus.UNAUTHORIZED);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String message = (user.getUserType() != null && user.getUserType() == 0) ? "管理员登录成功" : "用户登录成功";
            logger.info("Login successful: {}, Status: {}", message, HttpStatus.OK);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            String response = "服务器内部错误，请稍后再试";
            logger.error("Login error: {}, Status: {}", response, HttpStatus.INTERNAL_SERVER_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
