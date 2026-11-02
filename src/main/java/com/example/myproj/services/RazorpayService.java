package com.example.myproj.services;

import com.example.myproj.config.RazorpayProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Service
public class RazorpayService {

    private final RazorpayProperties properties;
    private final RestClient restClient;

    public RazorpayService(RazorpayProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.razorpay.com")
                .build();
    }

    public record RazorpayOrderCreated(String id, long amountPaise, String currency) {}

    public RazorpayOrderCreated createOrder(long amountPaise, String receipt) {
        requireConfigured();
        if (amountPaise < 100) {
            throw new IllegalStateException("Order amount must be at least 1.00 in currency units");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> body = restClient.post()
                .uri("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBasicAuth(properties.getKeyId(), properties.getKeySecret()))
                .body(Map.of(
                        "amount", amountPaise,
                        "currency", properties.getCurrency(),
                        "receipt", receipt,
                        "payment_capture", 1
                ))
                .retrieve()
                .body(Map.class);

        if (body == null || body.get("id") == null) {
            throw new IllegalStateException("Could not create Razorpay order");
        }

        return new RazorpayOrderCreated(
                String.valueOf(body.get("id")),
                ((Number) body.get("amount")).longValue(),
                String.valueOf(body.get("currency"))
        );
    }

    public boolean verifyPaymentSignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) {
        requireConfigured();
        if (razorpayOrderId == null || razorpayPaymentId == null || razorpaySignature == null) {
            return false;
        }
        String payload = razorpayOrderId + "|" + razorpayPaymentId;
        String expected = hmacSha256Hex(properties.getKeySecret(), payload);
        return expected.equalsIgnoreCase(razorpaySignature.trim());
    }

    public String newReceipt(Integer userId) {
        return "mp-" + userId + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public String getKeyId() {
        return properties.getKeyId();
    }

    public String getCurrency() {
        return properties.getCurrency();
    }

    public long toAmountPaise(double cartTotal) {
        return Math.round(cartTotal * 100.0);
    }

    private void requireConfigured() {
        if (!properties.isConfigured()) {
            throw new IllegalStateException(
                    "Razorpay test keys are not configured. Set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET.");
        }
    }

    private static String hmacSha256Hex(String secret, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Payment signature verification failed", e);
        }
    }
}
