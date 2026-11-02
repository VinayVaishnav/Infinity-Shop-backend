package com.example.myproj.models;

import com.example.myproj.enums.City;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer userId;
    String address;

    @Convert(converter = CityConverter.class)
    @Column(name = "city_id", nullable = false)
    City city;

    Integer pincode;

    @JsonProperty("cityLabel")
    public String getCityLabel() {
        return city == null ? null : city.getLabel();
    }
}
