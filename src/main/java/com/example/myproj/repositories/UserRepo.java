package com.example.myproj.repositories;

import com.example.myproj.models.UserInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserInfo, Integer> {

    @EntityGraph(attributePaths = "role")
    Optional<UserInfo> findByUserName(String userName);

    Optional<Object> findByUserNameAndEmail(String userName,String email);

    Optional<Object> findByUserNameOrEmail(String userName, String email);

    UserInfo findByUserId(Integer userId);
}
