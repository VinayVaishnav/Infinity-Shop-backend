package com.example.myproj.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateResponse {

    private String keyId;
    private String razorpayOrderId;
    private long amountPaise;
    private String currency;
    /** Cart total in major units (matches catalog prices). */
    private double cartTotal;
    private String prefillName;
    private String prefillEmail;
}
