package com.example.myproj.models;

import com.example.myproj.enums.ProductCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ProductCategoryConverter implements AttributeConverter<ProductCategory, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ProductCategory category) {
        return category == null ? null : category.getId();
    }

    @Override
    public ProductCategory convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return ProductCategory.fromId(id);
    }
}
