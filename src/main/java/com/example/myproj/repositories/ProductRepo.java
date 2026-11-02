package com.example.myproj.repositories;

import com.example.myproj.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {

    Product getProductById(Integer key);

    List<Product> findByCategory_IdAndNameContainsOrDescriptionContains(int category_id, String name, String description);

    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    List<Product> getProductByName(String name);
}
