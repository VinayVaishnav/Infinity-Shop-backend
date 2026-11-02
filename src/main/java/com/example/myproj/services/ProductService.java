package com.example.myproj.services;

import com.example.myproj.models.Product;
import com.example.myproj.models.UserInfo;
import com.example.myproj.repositories.ProductRepo;
import com.example.myproj.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    ProductRepo productRepo;

    @Autowired
    UserRepo userRepo;

    public List<Product> getProductList() {
        List<Product> productList = new ArrayList<>();
        productList.addAll(productRepo.findAll());
        return productList;
    }

    public Product getProductById(Integer productId) {
        return productRepo.getProductById(productId);
    }

    @Transactional(readOnly = true)
    public Map<Product, Integer> getCartDetails(String username) {
        UserInfo user = userRepo.findByUserName(username).orElseThrow();
        Map<Integer, Integer> cartItems = ensureCart(user);
        Map<Product, Integer> cartDetails = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            if (entry.getValue() <= 0) {
                continue;
            }
            Product prod = productRepo.getProductById(entry.getKey());
            if (prod != null) {
                cartDetails.put(prod, entry.getValue());
            }
        }

        return cartDetails;
    }

    @Transactional
    public void addToCart(Integer productId, String username) {
        Product product = productRepo.getProductById(productId);
        if (product == null) {
            throw new IllegalStateException("Product not found");
        }
        if (Boolean.FALSE.equals(product.getIsAvailable())) {
            throw new IllegalStateException("Product is not available");
        }
        int stock = product.getStock() == null ? 0 : product.getStock();
        UserInfo user = userRepo.findByUserName(username).orElseThrow();
        Map<Integer, Integer> cartItems = ensureCart(user);
        int inCart = cartItems.getOrDefault(productId, 0);
        if (inCart + 1 > stock) {
            throw new IllegalStateException("Not enough stock for this size");
        }
        cartItems.merge(productId, 1, Integer::sum);
        userRepo.save(user);
    }

    @Transactional
    public void removeFromCart(Integer productId, String username) {
        UserInfo user = userRepo.findByUserName(username).orElseThrow();
        Map<Integer, Integer> cartItems = ensureCart(user);
        int next = cartItems.getOrDefault(productId, 0) - 1;
        if (next <= 0) {
            cartItems.remove(productId);
        } else {
            cartItems.put(productId, next);
        }
        userRepo.save(user);
    }

    private static Map<Integer, Integer> ensureCart(UserInfo user) {
        if (user.getCart() == null) {
            user.setCart(new HashMap<>());
        }
        return user.getCart();
    }

    public List<Product> search(int categoryId, String keyword) {
        String term = keyword == null ? "" : keyword.trim();
        if (categoryId <= 0) {
            return productRepo.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(term, term);
        }
        return productRepo.findByCategory_IdAndNameContainsOrDescriptionContains(categoryId, term, term);
    }

    public double computeCartTotal(Map<Integer, Integer> cartItems) {
        double total = 0;
        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            if (entry.getValue() <= 0) {
                continue;
            }
            Product product = productRepo.getProductById(entry.getKey());
            if (product == null) {
                throw new IllegalStateException("Product not found: " + entry.getKey());
            }
            if (Boolean.FALSE.equals(product.getIsAvailable())) {
                throw new IllegalStateException("Product unavailable: " + product.getName());
            }
            double unitPrice = parsePrice(product.getPrice());
            total += unitPrice * entry.getValue();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    private static double parsePrice(String price) {
        if (price == null || price.isBlank()) {
            throw new IllegalStateException("Invalid product price");
        }
        try {
            return Double.parseDouble(price.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid product price: " + price);
        }
    }

    @Transactional
    public void decrementStockForOrder(Map<Integer, Integer> cartItems) {
        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            Product product = productRepo.getProductById(entry.getKey());
            if (product == null) {
                throw new IllegalStateException("Product not found: " + entry.getKey());
            }
            int qty = entry.getValue();
            int stock = product.getStock() == null ? 0 : product.getStock();
            if (qty > stock) {
                throw new IllegalStateException("Insufficient stock for " + product.getName() + " (" + product.getSize() + ")");
            }
            int remaining = stock - qty;
            product.setStock(remaining);
            if (remaining <= 0) {
                product.setStock(0);
                product.setIsAvailable(false);
            }
            productRepo.save(product);
        }
    }

    public void save(Product product) {
        productRepo.save(product);
    }

    public List<Product> getProduct(String name) {
        return productRepo.getProductByName(name);
    }
}
