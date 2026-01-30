package com.example.userauth.service;

import com.example.userauth.dto.CompanyCreateRequest;
import com.example.userauth.dto.CompanyAnalysisRequest;
import com.example.userauth.entity.*;
import com.example.userauth.exception.ApiException;
import com.example.userauth.repository.CompanyRepository;
import com.example.userauth.repository.UserRepository;
import com.example.userauth.repository.FinancialSpreadingRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final FinancialSpreadingRepository financialSpreadingRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompanyService(
            CompanyRepository companyRepository,
            UserRepository userRepository,
            FinancialSpreadingRepository financialSpreadingRepository
    ) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.financialSpreadingRepository = financialSpreadingRepository;
    }

    /* =====================================================
       LIST – ONLY COMPANIES CREATED BY LOGGED-IN USER
       ===================================================== */
    public List<Company> listByUser(Long userId) {
        return companyRepository.findByCreatedBy_Id(userId);
    }

    /* =====================================================
       CREATE
       ===================================================== */
    @Transactional
    public Company createCompany(CompanyCreateRequest req, Long creatorUserId) {

        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new ApiException("Creator user not found"));

        Company company = new Company();
        company.setCompanyName(req.getCompanyName());
        company.setCurrency(req.getCurrency());
        company.setBorrowerClassification(req.getBorrowerClassification());
        company.setDateOfAssessment(req.getDateOfAssessment());
        company.setRelationshipManager(req.getRelationshipManager());
        company.setIndustry(req.getIndustry());
        company.setCreatedBy(creator);

        /* -------- Owner Additional Support -------- */
        if (req.getOwnerAdditionalSupport() != null) {
            OwnerAdditionalSupport os = OwnerAdditionalSupport.builder()
                    .ownerPersonalNetWorth(
                            req.getOwnerAdditionalSupport().getOwnerPersonalNetWorth()
                    )
                    .build();
            company.setOwnerAdditionalSupport(os);
        }

        /* -------- Account Status -------- */
        if (req.getAccountStatus() != null) {
            CompanyCreateRequest.AccountStatusDTO as = req.getAccountStatus();
            AccountStatus acct = AccountStatus.builder()
                    .yearInBusiness(as.getYearInBusiness())
                    .locationOfBusiness(as.getLocationOfBusiness())
                    .relationshipAge(as.getRelationshipAge())
                    .auditorQuality(as.getAuditorQuality())
                    .auditorsOpinion(as.getAuditorsOpinion())
                    .nationalizationScheme(as.getNationalizationScheme())
                    .build();
            company.setAccountStatus(acct);
        }

        /* -------- Account Conduct -------- */
        if (req.getAccountConduct() != null) {
            CompanyCreateRequest.AccountConductDTO ac = req.getAccountConduct();
            AccountConduct conduct = AccountConduct.builder()
                    .bounceCheques(ac.getBounceCheques())
                    .ongoingCreditRelationship(ac.getOngoingCreditRelationship())
                    .delayInReceiptOfPrincipalProfitInstallments(
                            ac.getDelayInReceiptOfPrincipalProfitInstallments()
                    )
                    .numberOfDelinquencyInHistory(
                            ac.getNumberOfDelinquencyInHistory()
                    )
                    .writeOff(ac.getWriteOff())
                    .fraudAndLitigationIncidences(
                            ac.getFraudAndLitigationIncidences()
                    )
                    .build();
            company.setAccountConduct(conduct);
        }

        Company saved = companyRepository.save(company);
        saved.setBorrowerId(String.format("BRW%06d", saved.getId()));

        return companyRepository.save(saved);
    }

    /* =====================================================
       FIND BY ID
       ===================================================== */
    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    /* =====================================================
       FIND BY ID + USER
       ===================================================== */
    public Optional<Company> findByIdAndUser(Long id, Long userId) {
        return companyRepository.findByIdAndCreatedBy_Id(id, userId);
    }

    /* =====================================================
       DELETE
       ===================================================== */
    @Transactional
    public void deleteById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Company not found"));
        companyRepository.delete(company);
    }

    /* =====================================================
       DELETE BY USER
       ===================================================== */
    @Transactional
    public void deleteByIdAndUser(Long id, Long userId) {
        Company company = companyRepository
                .findByIdAndCreatedBy_Id(id, userId)
                .orElseThrow(() ->
                        new ApiException("Company not found or access denied"));

        companyRepository.delete(company);
    }

    /* =====================================================
       🔥 SAVE NORMALIZED FINANCIAL SPREADING
       ===================================================== */
@Transactional
public FinancialSpreading saveNormalizedSpreading(
    Long companyId,
    Long userId,
    CompanyAnalysisRequest request
) {
    Company company = companyRepository
        .findByIdAndCreatedBy_Id(companyId, userId)
        .orElseThrow(() -> 
            new ApiException("Company not found or access denied")
        );

    FinancialSpreading spreading = FinancialSpreading.builder()
        .company(company)
        .createdBy(userRepository.findById(userId).orElse(null))
        .build();

    /* ---------- YEAR MAPPING ---------- */
    Map<String, String> rawYearMapping = 
        request.getYearMapping() == null 
        ? Collections.emptyMap() 
        : request.getYearMapping();

    // Store year mappings
    for (Map.Entry<String, String> entry : rawYearMapping.entrySet()) {
        String label = entry.getKey();
        String yearStr = entry.getValue();
        Integer yearNumber = null;
        
        try {
            yearNumber = Integer.parseInt(yearStr);
        } catch (Exception ignored) {}
        
        SpreadingYear sy = SpreadingYear.builder()
            .label(label)
            .yearNumber(yearNumber)
            .spreading(spreading)
            .build();
        spreading.getYears().add(sy);
    }

    /* ---------- YEAR-WISE DATA ---------- */
    Map<String, Map<String, Map<String, String>>> yearWiseData = 
        request.getYearWiseData() == null 
        ? Collections.emptyMap() 
        : request.getYearWiseData();

    // Process each year
    for (Map.Entry<String, Map<String, Map<String, String>>> yearEntry : yearWiseData.entrySet()) {
        String year = yearEntry.getKey(); // e.g., "2020"
        Map<String, Map<String, String>> categories = yearEntry.getValue();
        
        Integer yearNumber = null;
        try {
            yearNumber = Integer.parseInt(year);
        } catch (Exception ignored) {}

        // Process each category for this year
        for (Map.Entry<String, Map<String, String>> catEntry : categories.entrySet()) {
            String categoryName = catEntry.getKey(); // e.g., "Current Assets"
            Map<String, String> items = catEntry.getValue();
            
            // Create or find category for this spreading
            SpreadingCategory category = spreading.getCategories().stream()
                .filter(c -> c.getName().equals(categoryName))
                .findFirst()
                .orElseGet(() -> {
                    SpreadingCategory newCat = SpreadingCategory.builder()
                        .name(categoryName)
                        .spreading(spreading)
                        .build();
                    spreading.getCategories().add(newCat);
                    return newCat;
                });

            // Process each item in this category
            for (Map.Entry<String, String> itemEntry : items.entrySet()) {
                String itemName = itemEntry.getKey(); // e.g., "Cash and bank balances"
                String valueStr = itemEntry.getValue();
                
                // Create or find item for this category
                SpreadingItem item = category.getItems().stream()
                    .filter(i -> i.getName().equals(itemName))
                    .findFirst()
                    .orElseGet(() -> {
                        SpreadingItem newItem = SpreadingItem.builder()
                            .name(itemName)
                            .category(category)
                            .build();
                        category.getItems().add(newItem);
                        return newItem;
                    });

                // Add value for this year to the item
                BigDecimal value = null;
                try {
                    if (valueStr != null && !valueStr.trim().isEmpty()) {
                        value = new BigDecimal(valueStr.trim());
                    }
                } catch (Exception ignored) {}

                SpreadingValue spreadingValue = SpreadingValue.builder()
                    .yearLabel(year)
                    .yearNumber(yearNumber)
                    .value(value)
                    .item(item)
                    .build();
                
                item.getValues().add(spreadingValue);
            }
        }
    }

    return financialSpreadingRepository.save(spreading);
}
}