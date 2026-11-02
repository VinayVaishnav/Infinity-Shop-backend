package com.example.myproj.controllers;

import com.example.myproj.enums.City;
import com.example.myproj.models.CityOptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/options")
public class OptionsController {

    @GetMapping("/cities")
    public ResponseEntity<List<CityOptionDto>> cityOptions() {
        List<CityOptionDto> options = Arrays.stream(City.values())
                .map(c -> new CityOptionDto(c.getId(), c.getLabel()))
                .toList();
        return ResponseEntity.ok(options);
    }
}
