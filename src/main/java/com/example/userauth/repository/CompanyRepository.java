package com.example.userauth.repository;

import com.example.userauth.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    // 🔐 Only companies created by this user
    List<Company> findByCreatedBy_Id(Long userId);

    // 🔐 One company owned by user
    Optional<Company> findByIdAndCreatedBy_Id(Long id, Long userId);

    // 🔐 Delete only if owned by user
    void deleteByIdAndCreatedBy_Id(Long id, Long userId);
}
