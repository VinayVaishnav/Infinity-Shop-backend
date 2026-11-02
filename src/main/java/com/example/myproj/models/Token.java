package com.example.myproj.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Token {
    public RefreshToken refreshToken;
    public jwtToken jwtToken;
}
