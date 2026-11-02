package com.example.myproj.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderAdminDto {
    private Order order;
    private Address address;
    private UserInfo userInfo;
}
