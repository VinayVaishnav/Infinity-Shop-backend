package com.example.myproj.services;


import com.example.myproj.models.*;
import com.example.myproj.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    public UserRepo userRepo;

    @Autowired
    public tokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Token login(User user) {
        Optional<UserInfo> userInfo = getUserInfo(user);
        UserInfo persisted = userInfo.orElseThrow();
        user.setId(persisted.getUserId());
        user.setUsername(persisted.getUserName());
        user.setRole(persisted.getRole());

        Token token = new Token();
        token.setJwtToken(tokenService.generateJwtToken(user));
        token.setRefreshToken(tokenService.generateRefreshToken(persisted));
        return token;
    }

    public Optional<UserInfo> getUserInfo(User user) {
        return userRepo.findByUserName(user.getUsername());
    }

    public Token signIn(UserInfo userInfo) {
        if (userInfo.getRole() == null) {
            userInfo.setRole(Role.builder().id(2).name("USER").build());
        }
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        UserInfo saved = userRepo.save(userInfo);

        User user = toUser(saved);

        Token token = new Token();
        token.setJwtToken(tokenService.generateJwtToken(user));
        token.setRefreshToken(tokenService.generateRefreshToken(saved));
        return token;
    }

    public boolean userExists(User user) {
        return userRepo.findByUserName(user.getUsername()).isPresent();
    }

    public Role getRole(User user) {
        Optional<UserInfo> userInfo = userRepo.findByUserName(user.getUsername());
        return userInfo.map(UserInfo::getRole).orElse(null);
    }

    public Optional<UserInfo> getUserInfoByUsername(String username) {
        return userRepo.findByUserName(username)
                .map(info -> {
                    info.setPassword(null);
                    return info;
                });
    }

    private static User toUser(UserInfo userInfo) {
        User user = new User();
        user.setId(userInfo.getUserId());
        user.setUsername(userInfo.getUserName());
        user.setRole(userInfo.getRole());
        user.setPassword(userInfo.getPassword());
        return user;
    }

    public boolean userExistsWithDetails(UserInfo userdata) {
        return userRepo.findByUserNameOrEmail(userdata.getUserName(),userdata.getEmail()).isPresent();
    }

    public void updateUserInfo(UserInfo patch, String username) {
        UserInfo existing = userRepo.findByUserName(username).orElseThrow();
        if (patch.getFirstName() != null) {
            existing.setFirstName(patch.getFirstName());
        }
        if (patch.getLastName() != null) {
            existing.setLastName(patch.getLastName());
        }
        userRepo.save(existing);
    }

    public UserInfo getUserInfoByUserId(Integer userId) {
        UserInfo userInfo=userRepo.findByUserId(userId);
        return userInfo;
    }
}
