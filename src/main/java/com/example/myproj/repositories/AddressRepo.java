package com.example.myproj.repositories;

import com.example.myproj.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepo extends JpaRepository<Address,Integer> {
    List<Address> findAllByUserId(Integer userId);
}
