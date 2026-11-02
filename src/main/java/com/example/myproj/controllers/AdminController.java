package com.example.myproj.controllers;

import com.example.myproj.models.Order;
import com.example.myproj.models.OrderAdminDto;
import com.example.myproj.models.Product;
import com.example.myproj.services.OrderService;
import com.example.myproj.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> dashboard() {
        return ResponseEntity.ok(Map.of("message", "Welcome, admin"));
    }

    @PostMapping("/product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> product(@RequestBody Product product) {
        try {
            productService.save(product);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/allOrders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderAdminDto>> getOrders() {
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }

    @PostMapping("/order/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updateOrderStatus(@RequestBody Map<String, ?> body) {
        int orderId = ((Number) body.get("orderId")).intValue();
        String status = (String) body.get("status");
        Order updated = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updated);
    }
}
