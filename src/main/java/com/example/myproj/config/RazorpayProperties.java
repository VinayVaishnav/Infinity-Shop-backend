package com.example.myproj.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "razorpay")
public class RazorpayProperties {

    /** Test or live key id (public in checkout UI). */
    private String keyId = "";

    /** Test or live key secret — server only. */
    private String keySecret = "";

    /** ISO currency for Razorpay orders (test mode typically INR). */
    private String currency = "INR";

    public boolean isConfigured() {
        return keyId != null && !keyId.isBlank()
                && keySecret != null && !keySecret.isBlank();
    }
}
