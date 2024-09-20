package com.izijin.cmp.controller;

import com.izijin.cmp.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiSm9obiIsImFkbWluIjp0cnVlfQ.VrbK2hC5k7mp8hiRo__zxUyDVLE1OOO-cyTHNYpzIdQ")
    private String tempMigrationSecret;

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/migrate-passwords")
    public ResponseEntity<?> migratePasswords() {
        try {
            userService.migrateAllPasswords();
            return ResponseEntity.ok("所有密码已成功迁移到加密形式");
        } catch (Exception e) {
            //logger.error("密码迁移失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("密码迁移失败：" + e.getMessage());
        }
    }
}