package com.example.myproj.models;

import com.example.myproj.enums.City;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CityConverter implements AttributeConverter<City, Integer> {

    @Override
    public Integer convertToDatabaseColumn(City city) {
        return city == null ? null : city.getId();
    }

    @Override
    public City convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return City.fromId(id);
    }
}
