package com.example.myproj.services;

import com.example.myproj.enums.OrderStatus;
import com.example.myproj.models.Address;
import com.example.myproj.models.CartItemDto;
import com.example.myproj.models.Order;
import com.example.myproj.models.OrderAdminDto;
import com.example.myproj.models.OrderDetailDto;
import com.example.myproj.models.PaymentCreateResponse;
import com.example.myproj.models.Product;
import com.example.myproj.models.UserInfo;
import com.example.myproj.repositories.OrderRepo;
import com.example.myproj.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.time.Instant.now;

@Service
public class OrderService {

    private static final double TOTAL_TOLERANCE = 0.02;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ProductService productService;

    @Autowired
    AddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    RazorpayService razorpayService;

    @Transactional(readOnly = true)
    public PaymentCreateResponse createRazorpayPayment(String username, Integer addressId, Double clientAmount) {
        CheckoutContext ctx = validateCheckoutContext(username, addressId, clientAmount);

        long amountPaise = razorpayService.toAmountPaise(ctx.computedTotal());
        RazorpayService.RazorpayOrderCreated razorpayOrder = razorpayService.createOrder(
                amountPaise,
                razorpayService.newReceipt(ctx.user().getUserId())
        );

        String displayName = buildDisplayName(ctx.user());

        return PaymentCreateResponse.builder()
                .keyId(razorpayService.getKeyId())
                .razorpayOrderId(razorpayOrder.id())
                .amountPaise(razorpayOrder.amountPaise())
                .currency(razorpayOrder.currency())
                .cartTotal(ctx.computedTotal())
                .prefillName(displayName)
                .prefillEmail(ctx.user().getEmail())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public Order confirmCheckoutWithRazorpay(
            String username,
            Double clientAmount,
            Integer addressId,
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) {
        if (!razorpayService.verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature)) {
            throw new IllegalStateException("Invalid payment signature");
        }
        if (orderRepo.findByRazorpayPaymentId(razorpayPaymentId).isPresent()) {
            throw new IllegalStateException("This payment was already used for an order");
        }

        CheckoutContext ctx = validateCheckoutContext(username, addressId, clientAmount);

        return completeCheckout(
                ctx,
                razorpayOrderId,
                razorpayPaymentId,
                OrderStatus.CONFIRMED
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public Order checkout(String username, Double clientAmount, Integer addressId) {
        CheckoutContext ctx = validateCheckoutContext(username, addressId, clientAmount);
        return completeCheckout(ctx, null, null, OrderStatus.PENDING);
    }

    private CheckoutContext validateCheckoutContext(String username, Integer addressId, Double clientAmount) {
        UserInfo user = userRepo.findByUserName(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (addressId == null) {
            throw new IllegalStateException("Delivery address is required");
        }

        Address address = addressService.getAddressById(addressId);
        if (address == null) {
            throw new IllegalStateException("Address not found");
        }
        if (!address.getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("Address does not belong to user");
        }

        Map<Integer, Integer> cartItems = user.getCart() != null
                ? new HashMap<>(user.getCart())
                : Map.of();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        double computedTotal = productService.computeCartTotal(cartItems);
        if (clientAmount != null
                && Math.abs(computedTotal - clientAmount) > TOTAL_TOLERANCE) {
            throw new IllegalStateException(
                    "Order total mismatch. Expected " + computedTotal + " but received " + clientAmount);
        }

        return new CheckoutContext(user, cartItems, computedTotal, addressId);
    }

    private Order completeCheckout(
            CheckoutContext ctx,
            String razorpayOrderId,
            String razorpayPaymentId,
            OrderStatus status
    ) {
        productService.decrementStockForOrder(ctx.cartItems());

        Order order = Order.builder()
                .userId(ctx.user().getUserId())
                .totalPrice(ctx.computedTotal())
                .addressId(ctx.addressId())
                .products(new HashMap<>(ctx.cartItems()))
                .createTime(now())
                .status(status.name())
                .razorpayOrderId(razorpayOrderId)
                .razorpayPaymentId(razorpayPaymentId)
                .build();
        Order saved = orderRepo.save(order);

        ctx.user().getCart().clear();
        userRepo.save(ctx.user());

        return saved;
    }

    private static String buildDisplayName(UserInfo user) {
        String first = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String last = user.getLastName() != null ? user.getLastName().trim() : "";
        String combined = (first + " " + last).trim();
        if (!combined.isEmpty()) {
            return combined;
        }
        return user.getUserName();
    }

    @Transactional(readOnly = true)
    public OrderDetailDto getOrderDetail(Integer orderId, String username) {
        UserInfo user = userRepo.findByUserName(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        if (!Objects.equals(order.getUserId(), user.getUserId())) {
            throw new IllegalStateException("Order does not belong to user");
        }

        Address address = order.getAddressId() != null
                ? addressService.getAddressById(order.getAddressId())
                : null;

        List<CartItemDto> lineItems = buildLineItems(order.getProducts());

        return new OrderDetailDto(order, address, lineItems);
    }

    private List<CartItemDto> buildLineItems(Map<Integer, Integer> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }
        List<CartItemDto> items = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : products.entrySet()) {
            Product product = productService.getProductById(entry.getKey());
            items.add(new CartItemDto(product, entry.getValue()));
        }
        items.sort(Comparator.comparing(
                item -> item.getProduct() != null ? item.getProduct().getName() : "",
                String.CASE_INSENSITIVE_ORDER
        ));
        return items;
    }

    private record CheckoutContext(
            UserInfo user,
            Map<Integer, Integer> cartItems,
            double computedTotal,
            Integer addressId
    ) {}

    @Transactional(readOnly = true)
    public List<Order> getOrders(String username) {
        Optional<UserInfo> user = userRepo.findByUserName(username);
        if (user.isEmpty()) {
            return List.of();
        }
        return orderRepo.findAllByUserId(user.get().getUserId());
    }

    @Transactional(readOnly = true)
    public List<OrderAdminDto> getAllOrdersForAdmin() {
        List<Order> orders = orderRepo.findAllByOrderByCreateTimeDesc();
        List<OrderAdminDto> result = new ArrayList<>();

        for (Order order : orders) {
            Address address = order.getAddressId() != null
                    ? addressService.getAddressById(order.getAddressId())
                    : null;
            UserInfo userInfo = userService.getUserInfoByUserId(order.getUserId());
            if (userInfo != null) {
                userInfo.setPassword(null);
            }
            result.add(new OrderAdminDto(order, address, userInfo));
        }
        return result;
    }

    @Transactional
    public Order updateOrderStatus(Integer orderId, String statusValue) {
        OrderStatus status = OrderStatus.fromString(statusValue);
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        order.setStatus(status.name());
        return orderRepo.save(order);
    }
}
