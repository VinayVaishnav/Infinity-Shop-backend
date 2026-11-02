package com.example.myproj.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mp_orders")
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name;

    @ElementCollection
    @CollectionTable(name = "mp_order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    Map<Integer,Integer> products;
    Integer userId;
    Double totalPrice;
    Instant createTime;
    Integer addressId;
    String status;
    String razorpayOrderId;
    String razorpayPaymentId;
}
