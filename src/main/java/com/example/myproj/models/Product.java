package com.example.myproj.models;

import com.example.myproj.enums.ProductCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Version;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "mp_products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name;
    String description;

    @Convert(converter = ProductCategoryConverter.class)
    @Column(name = "category_id", nullable = false)
    ProductCategory category;

    String price;
    String image;

    @JsonProperty("categoryLabel")
    public String getCategoryLabel() {
        return category == null ? null : category.getLabel();
    }

    @Version
    @Column(name = "version")
    Long version;

    Boolean isAvailable;
    Integer stock;
    String size;
}
