package com.example.userauth.repository;

import com.example.userauth.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByBorrowerId(String borrowerId);
}
