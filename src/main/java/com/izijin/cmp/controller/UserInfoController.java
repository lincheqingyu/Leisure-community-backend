package com.izijin.cmp.controller;

import com.izijin.cmp.model.User;
import com.izijin.cmp.service.UserInfoService;
import com.izijin.cmp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/api/user-info")
    public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7)); // 去掉 "Bearer " 前缀
        User user = userInfoService.getUserInfo(username);
        return ResponseEntity.ok(user);
    }
}