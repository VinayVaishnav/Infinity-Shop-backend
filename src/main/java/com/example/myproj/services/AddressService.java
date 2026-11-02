package com.example.myproj.services;

import com.example.myproj.models.Address;
import com.example.myproj.models.UserInfo;
import com.example.myproj.repositories.AddressRepo;
import com.example.myproj.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    AddressRepo addressRepo;

    @Autowired
    UserRepo userRepo;

    public List<Address> getAllAddressByUserName(String userName){
        Optional<UserInfo> userInfo = userRepo.findByUserName(userName);
        if(userInfo.isPresent()){
            return addressRepo.findAllByUserId(userInfo.get().userId);
        }
        return null;
    }

    public void upsertAddress(Address address,String username){
        Optional<UserInfo> userInfo = userRepo.findByUserName(username);
        if(userInfo.isPresent()){
            address.setUserId(userInfo.get().userId);
            addressRepo.save(address);
        }
    }

    public void deleteAddress(Integer addressId, String username) {
        UserInfo user = userRepo.findByUserName(username).orElseThrow();
        Address address = addressRepo.findById(addressId).orElseThrow();
        if (!address.getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Address does not belong to user");
        }
        addressRepo.delete(address);
    }

    public Address getAddressById(Integer addressId){
        Optional<Address> address = addressRepo.findById(addressId);
        if(address.isPresent()){
            return address.get();
        }
        return null;
    }

}
