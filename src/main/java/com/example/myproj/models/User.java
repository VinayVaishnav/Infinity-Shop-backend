package com.example.myproj.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public Integer id;

    public String username;

    public String password;

    public Role role;
}
