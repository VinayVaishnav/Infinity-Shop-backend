package com.example.myproj.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ProductCategory {
    T_SHIRT(1, "T-Shirts"),
    SHIRT(2, "Shirts"),
    SHOES(3, "Shoes"),
    HOODIE(4, "Hoodies"),
    ACCESSORY(5, "Accessories"),
    CAP(6, "Caps");

    private final int id;
    private final String label;

    ProductCategory(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @JsonValue
    public int toJson() {
        return id;
    }

    @JsonCreator
    public static ProductCategory fromId(int id) {
        return Arrays.stream(values())
                .filter(c -> c.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown product category id: " + id));
    }
}
