package com.example.myproj.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="mp_users")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    public Role role;
    public String token;
    public String refreshToken;
    public String userName;
    public String firstName;
    public String lastName;
    public String email;
    public String password;

    @ElementCollection
    @CollectionTable(name = "mp_user_cart", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    @Builder.Default
    public Map<Integer, Integer> cart = new HashMap<>();
}
