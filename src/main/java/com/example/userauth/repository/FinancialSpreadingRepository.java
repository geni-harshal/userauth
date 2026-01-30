package com.example.userauth.repository;

import com.example.userauth.entity.FinancialSpreading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FinancialSpreadingRepository extends JpaRepository<FinancialSpreading, Long> {

    List<FinancialSpreading> findByCompany_Id(Long companyId);
}
