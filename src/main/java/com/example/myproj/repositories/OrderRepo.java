package com.example.myproj.repositories;

import com.example.myproj.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order,Integer> {
    List<Order> findAllByUserId(Integer userId);

    List<Order> findAllByOrderByCreateTimeDesc();

    Optional<Order> findByRazorpayPaymentId(String razorpayPaymentId);

    Order getOrderById(Integer orderId);
}
