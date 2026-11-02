package com.example.myproj.controllers;

import com.example.myproj.models.Product;
import com.example.myproj.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/products/")
public class ProductController {


    @Autowired
    private ProductService productService;


    @GetMapping("all")
    public ResponseEntity<List<Product>> getAllProducts(){
        List<Product> products=productService.getProductList();
        return new ResponseEntity<>(products,HttpStatus.OK);
    }

    @GetMapping("product")
    public ResponseEntity<List<Product>> getProduct(@RequestParam String name) {
        List<Product> product = productService.getProduct(name);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("addToCart")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> addToCart(@RequestBody Map<String, ?> map) {
        try {
            int prodId = ((Number) map.get("prodId")).intValue();
            String username = (String) map.get("username");
            productService.addToCart(prodId, username);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("removeFromCart")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> removeFromCart(@RequestBody Map<String, ?> map) {
        int prodId = ((Number) map.get("prodId")).intValue();
        String username = (String) map.get("username");
        productService.removeFromCart(prodId, username);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PostMapping("search")
    public ResponseEntity<List<Product>> searchProduct(@RequestBody Map<String, ?> map) {
        int categoryId = ((Number) map.get("categoryId")).intValue();
        String keyword = (String) map.get("keyword");
        List<Product> result= productService.search(categoryId, keyword);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
