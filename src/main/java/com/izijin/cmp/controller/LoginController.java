package com.izijin.cmp.controller;

import com.izijin.cmp.model.User;
import com.izijin.cmp.service.UserService;
import com.izijin.cmp.util.JwtUtil;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        logger.info("LoginController 已初始化");
        logger.info("AuthenticationManager 注入状态: {}", (authenticationManager != null ? "成功" : "失败"));
        logger.info("JwtUtil 注入状态: {}", (jwtUtil != null ? "成功" : "失败"));
        logger.info("UserService 注入状态: {}", (userService != null ? "成功" : "失败"));
    }

    /**
     * 处理用户登录请求
     * @param loginRequest 包含用户名和密码的Map
     * @return ResponseEntity 包含登录结果的响应实体
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        logger.info("收到登录请求");
        
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 验证用户名和密码是否为空
        if (username == null || password == null) {
            String response = "用户名和密码不能为空";
            logger.info("登录失败: {}, 状态: {}", response, HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            logger.info("尝试认证用户: {}", username);
            // 尝试认证用户
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            logger.info("用户认证成功: {}", username);

            // 获取用户信息并生成token
            User user = userService.findByUsername(username);
            String token = jwtUtil.generateToken(username);

            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);

            // 根据用户类型记录不同的登录成功信息
            String message = (user.getUserType() != null && user.getUserType() == 0) ? "管理员登录成功" : "用户登录成功";
            logger.info("登录成功: {}, 状态: {}, 用户: {}", message, HttpStatus.OK, username);
            
            // 在返回响应之前，再次确认token已经包含在响应中
            logger.debug("响应中包含token: {}", response.containsKey("token"));
            
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            // 认证失败的情况
            logger.warn("用户认证失败: {}", username, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        } catch (Exception e) {
            // 其他异常情况
            logger.error("登录过程中发生意外错误，用户: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误，请稍后再试");
        }
    }
}
