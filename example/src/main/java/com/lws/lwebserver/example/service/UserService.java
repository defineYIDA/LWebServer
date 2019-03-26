package com.lws.lwebserver.example.service;

import com.lws.lwebserver.example.model.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:53
 */
@Slf4j
public class UserService {
    private static UserService instance = new UserService();

    public static UserService getInstance() {
        return instance;
    }

    private Map<String, User> users = new ConcurrentHashMap<>();
    private Map<String, String> online = new ConcurrentHashMap<>();


    public UserService() {
        users.put("admin", new User("admin", "admin", "管理员", 20));
        users.put("user", new User("user", "123456", "用户", 23));
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (password.equals(user.getPassword())) {
            online.put(username, "");
            return true;
        }
        return false;
    }

    public User findByUsername(String username) {
        return users.get(username);
    }


    public void update(User user) {
        users.put(user.getUsername(),user);
    }
}
