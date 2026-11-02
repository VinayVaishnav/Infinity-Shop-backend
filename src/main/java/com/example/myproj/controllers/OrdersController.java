package com.example.myproj.controllers;

import com.example.myproj.models.Order;
import com.example.myproj.models.OrderDetailDto;
import com.example.myproj.models.PaymentCreateResponse;
import com.example.myproj.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/order")
public class OrdersController {

    @Autowired
    OrderService orderService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Order>> getOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Order> orders = orderService.getOrders(username);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Order> checkout(@RequestBody Map<String, ?> data) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Double amount = data.get("amount") != null
                ? ((Number) data.get("amount")).doubleValue()
                : null;
        int addressId = ((Number) data.get("addressId")).intValue();
        Order order = orderService.checkout(username, amount, addressId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/payment/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaymentCreateResponse> createPayment(@RequestBody Map<String, ?> data) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Double amount = data.get("amount") != null
                ? ((Number) data.get("amount")).doubleValue()
                : null;
        int addressId = ((Number) data.get("addressId")).intValue();
        PaymentCreateResponse response = orderService.createRazorpayPayment(username, addressId, amount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment/verify")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Order> verifyPayment(@RequestBody Map<String, ?> data) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Double amount = data.get("amount") != null
                ? ((Number) data.get("amount")).doubleValue()
                : null;
        int addressId = ((Number) data.get("addressId")).intValue();
        String razorpayOrderId = (String) data.get("razorpayOrderId");
        String razorpayPaymentId = (String) data.get("razorpayPaymentId");
        String razorpaySignature = (String) data.get("razorpaySignature");
        Order order = orderService.confirmCheckoutWithRazorpay(
                username,
                amount,
                addressId,
                razorpayOrderId,
                razorpayPaymentId,
                razorpaySignature
        );
        return ResponseEntity.ok(order);
    }

    @GetMapping("/getOrder")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderDetailDto> getOrder(@RequestParam("orderId") Integer orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        OrderDetailDto detail = orderService.getOrderDetail(orderId, username);
        return ResponseEntity.ok(detail);
    }
}
