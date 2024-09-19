package com.izijin.cmp.service;

import com.izijin.cmp.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    
    public List<User> getAllStudents(Pageable pageable) {
        // 这里应该实现从数据库获取学生列表的逻辑
        // 暂时返回空列表
        return List.of();
    }

    // 其他方法...
}