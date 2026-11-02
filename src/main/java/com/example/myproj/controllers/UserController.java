package com.example.myproj.controllers;

import com.example.myproj.models.Address;
import com.example.myproj.models.CartItemDto;
import com.example.myproj.models.Product;
import com.example.myproj.models.UserInfo;
import com.example.myproj.services.AddressService;
import com.example.myproj.services.ProductService;
import com.example.myproj.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    AddressService addressService;

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getUserInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userService.getUserInfoByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/cartDetails")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CartItemDto>> getCartDetails() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<Product, Integer> cartDetails = productService.getCartDetails(username);
        List<CartItemDto> items = cartDetails.entrySet().stream()
                .map(e -> new CartItemDto(e.getKey(), e.getValue()))
                .toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getAddresses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Address> addresses= addressService.getAllAddressByUserName(username);
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/address")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> addAddress(@RequestBody Address address) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        addressService.upsertAddress(address, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/address/{addressId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer addressId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        addressService.deleteAddress(addressId, username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateUserDetails(@RequestBody UserInfo userInfo) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.updateUserInfo(userInfo, username);
        return ResponseEntity.ok().build();
    }
}
