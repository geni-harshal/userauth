package com.example.userauth.controller;

import com.example.userauth.dto.*;
import com.example.userauth.entity.Company;
import com.example.userauth.service.CompanyService;
import com.example.userauth.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // Create a company (protected route)
    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(@RequestBody CompanyCreateRequest req,
                                                                      HttpServletRequest request) {
        // read logged-in user from AuthFilter attribute "authUser" -> "userId|email"
        Object authUser = request.getAttribute("authUser");
        if (authUser == null) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Unauthorized", null));
        }
        String s = authUser.toString();
        Long userId = Long.parseLong(s.split("\\|")[0]);

        Company saved = companyService.createCompany(req, userId);

        CompanyResponse resp = new CompanyResponse(saved.getId(), saved.getBorrowerId(), saved.getCompanyName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Company created", resp));
    }

    // Simple get by id (existing)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(@PathVariable Long id) {
        return companyService.findById(id)
                .map(c -> ResponseEntity.ok(new ApiResponse<>(true, "Company found",
                        new CompanyResponse(c.getId(), c.getBorrowerId(), c.getCompanyName()))))
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>(false, "Company not found", null)));
    }

    // B: LIST endpoint for dashboard - returns lightweight list items
    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyListItem>>> listCompanies() {
        List<Company> all = companyService.listAll();
        List<CompanyListItem> dto = all.stream()
                .map(c -> new CompanyListItem(c.getId(),
                                             c.getBorrowerId(),
                                             c.getCompanyName(),
                                             c.getIndustry(),
                                             c.getCurrency()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Companies list", dto));
    }

    // C: DETAILS endpoint - returns full details including qualitative inputs
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<CompanyDetailsDTO>> getCompanyDetails(@PathVariable Long id) {
        return companyService.findById(id)
                .map(c -> {
                    CompanyDetailsDTO d = mapToDetails(c);
                    return ResponseEntity.ok(new ApiResponse<>(true, "Company details", d));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>(false, "Company not found", null)));
    }

    // helper to map entity -> DTO
    private CompanyDetailsDTO mapToDetails(Company c) {
        CompanyDetailsDTO d = new CompanyDetailsDTO();

        // core
        d.setId(c.getId());
        d.setBorrowerId(c.getBorrowerId());
        d.setCompanyName(c.getCompanyName());
        d.setCurrency(c.getCurrency());
        d.setBorrowerClassification(c.getBorrowerClassification());
        d.setDateOfAssessment(c.getDateOfAssessment());
        d.setRelationshipManager(c.getRelationshipManager());
        d.setIndustry(c.getIndustry());
        d.setCreatedAt(c.getCreatedAt());
        d.setUpdatedAt(c.getUpdatedAt());

        if (c.getCreatedBy() != null) {
            d.setCreatedByUserId(c.getCreatedBy().getId());
            d.setCreatedByEmail(c.getCreatedBy().getEmail());
        }

        // owner additional support
        if (c.getOwnerAdditionalSupport() != null) {
            d.setOwnerPersonalNetWorth(c.getOwnerAdditionalSupport().getOwnerPersonalNetWorth());
        }

        // account status
        if (c.getAccountStatus() != null) {
            d.setYearInBusiness(c.getAccountStatus().getYearInBusiness());
            d.setLocationOfBusiness(c.getAccountStatus().getLocationOfBusiness());
            d.setRelationshipAge(c.getAccountStatus().getRelationshipAge());
            d.setAuditorQuality(c.getAccountStatus().getAuditorQuality());
            d.setAuditorsOpinion(c.getAccountStatus().getAuditorsOpinion());
            d.setNationalizationScheme(c.getAccountStatus().getNationalizationScheme());
        }

        // account conduct
        if (c.getAccountConduct() != null) {
            d.setBounceCheques(c.getAccountConduct().getBounceCheques());
            d.setOngoingCreditRelationship(c.getAccountConduct().getOngoingCreditRelationship());
            d.setDelayInReceiptOfPrincipalProfitInstallments(c.getAccountConduct().getDelayInReceiptOfPrincipalProfitInstallments());
            d.setNumberOfDelinquencyInHistory(c.getAccountConduct().getNumberOfDelinquencyInHistory());
            d.setWriteOff(c.getAccountConduct().getWriteOff());
            d.setFraudAndLitigationIncidences(c.getAccountConduct().getFraudAndLitigationIncidences());
        }

        return d;
    }
}
