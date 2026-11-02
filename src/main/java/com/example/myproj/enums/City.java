package com.example.myproj.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum City {
    MUMBAI(1, "Mumbai"),
    NEW_DELHI(2, "New Delhi"),
    BANGALORE(3, "Bengaluru"),
    HYDERABAD(4, "Hyderabad"),
    CHENNAI(5, "Chennai"),
    KOLKATA(6, "Kolkata"),
    PUNE(7, "Pune"),
    AHMEDABAD(8, "Ahmedabad"),
    JAIPUR(9, "Jaipur"),
    LUCKNOW(10, "Lucknow");

    private final int id;
    private final String label;

    City(int id, String label) {
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
    public static City fromId(int id) {
        return Arrays.stream(values())
                .filter(c -> c.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown city id: " + id));
    }
}
