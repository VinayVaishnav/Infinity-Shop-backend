package com.example.myproj.controllers;

import com.example.myproj.models.Role;
import com.example.myproj.models.Token;
import com.example.myproj.models.User;
import com.example.myproj.models.UserInfo;
import com.example.myproj.services.UserService;
import com.example.myproj.services.tokenService;
import com.example.myproj.services.tokenService.TokenAuthResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
@Slf4j
public class AuthController {

    private final BCryptPasswordEncoder passwordEncoder;
    public AuthController(BCryptPasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    static Map<String,Integer> RoleMap = new HashMap<>();
    static {
        RoleMap.put("ADMIN",1);
        RoleMap.put("USER",2);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private tokenService tokenService;

    @PostMapping(value = "login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(@RequestBody Map<String,String> map) {

        User user=User.builder().username(map.get("username")).password(map.get("password")).build();

        if(!userService.userExists(user)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        else {
            Optional<UserInfo> info = userService.getUserInfo(user);
            if(info.isPresent() && !passwordEncoder.matches(user.getPassword(),info.get().getPassword())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        Token token =userService.login(user);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody UserInfo userdata) {

        if(userService.userExistsWithDetails(userdata)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        UserInfo userInfo=userdata;
        Token token=userService.signIn(userInfo);

        return new ResponseEntity<>(token,HttpStatus.OK);
    }

    @GetMapping("/auth")
    public ResponseEntity<?> checkAuth(
            @RequestHeader("jwtToken") String jwtToken,
            @RequestHeader("refreshToken") String refreshToken) {

        log.info("jwtToken:{} refreshToken:{}",jwtToken,refreshToken);
        TokenAuthResult auth = tokenService.authenticate(jwtToken, refreshToken);

        return switch (auth.status()) {
            case VALID -> ResponseEntity.ok().build();
            case REFRESHED -> ResponseEntity.ok(auth.newJwtToken());
            case UNAUTHORIZED -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        };
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            tokenService.revokeRefreshToken(refreshToken);
        }
        return ResponseEntity.noContent().build();
    }
}
