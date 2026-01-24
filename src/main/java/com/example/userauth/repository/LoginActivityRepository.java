package com.example.userauth.repository;

import com.example.userauth.entity.LoginActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginActivityRepository extends JpaRepository<LoginActivity, Long> {

    Optional<LoginActivity> findTopByUserIdOrderByLoginTimeDesc(Long userId);
}
