package com.izijin.cmp.controller;

import com.izijin.cmp.model.User;
import com.izijin.cmp.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@CrossOrigin(origins = {"http://localhost:3000"})
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        System.out.println("Received registration request: " + user);
        try {
            User registeredUser = registerService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细错误信息到控制台
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("注册过程中发生错误：" + e.getMessage());
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsernameExists(@RequestParam String username) {
        boolean exists = registerService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }
}
