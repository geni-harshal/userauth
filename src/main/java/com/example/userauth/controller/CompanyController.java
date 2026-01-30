package com.example.userauth.controller;

import com.example.userauth.dto.*;
import com.example.userauth.entity.Company;
import com.example.userauth.entity.FinancialSpreading;
import com.example.userauth.service.CompanyService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /* ===============================
       CREATE
       =============================== */
    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @RequestBody CompanyCreateRequest req,
            HttpServletRequest request
    ) {
        Object authUser = request.getAttribute("authUser");
        if (authUser == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "Unauthorized", null));
        }

        Long userId = Long.parseLong(authUser.toString().split("\\|")[0]);
        Company saved = companyService.createCompany(req, userId);

        CompanyResponse resp = new CompanyResponse(
                saved.getId(),
                saved.getBorrowerId(),
                saved.getCompanyName()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Company created", resp)
        );
    }

    /* ===============================
       DELETE
       =============================== */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Object authUser = request.getAttribute("authUser");
        if (authUser == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "Unauthorized", null));
        }

        Long userId = Long.parseLong(authUser.toString().split("\\|")[0]);
        companyService.deleteByIdAndUser(id, userId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Company deleted successfully", null)
        );
    }

    /* ===============================
       GET
       =============================== */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Object authUser = request.getAttribute("authUser");
        if (authUser == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "Unauthorized", null));
        }

        Long userId = Long.parseLong(authUser.toString().split("\\|")[0]);

        return companyService.findByIdAndUser(id, userId)
                .map(c -> ResponseEntity.ok(
                        new ApiResponse<>(true, "Company found",
                                new CompanyResponse(
                                        c.getId(),
                                        c.getBorrowerId(),
                                        c.getCompanyName()
                                ))
                ))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ApiResponse<>(false, "Company not found", null)));
    }

    /* ===============================
       LIST
       =============================== */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyListItem>>> listCompanies(
            HttpServletRequest request
    ) {
        Object authUser = request.getAttribute("authUser");
        if (authUser == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "Unauthorized", null));
        }

        Long userId = Long.parseLong(authUser.toString().split("\\|")[0]);

        List<CompanyListItem> dto = companyService.listByUser(userId)
                .stream()
                .map(c -> new CompanyListItem(
                        c.getId(),
                        c.getBorrowerId(),
                        c.getCompanyName(),
                        c.getIndustry(),
                        c.getCurrency()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "My companies", dto)
        );
    }

    /* ===============================
       DETAILS
       =============================== */
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<CompanyDetailsDTO>> getCompanyDetails(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Object authUser = request.getAttribute("authUser");
        if (authUser == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "Unauthorized", null));
        }

        Long userId = Long.parseLong(authUser.toString().split("\\|")[0]);

        return companyService.findByIdAndUser(id, userId)
                .map(c -> ResponseEntity.ok(
                        new ApiResponse<>(true, "Company details", mapToDetails(c))
                ))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ApiResponse<>(false, "Company not found", null)));
    }

    /* ===============================
       GET RAW ANALYSIS (JSON)
       =============================== */
    @GetMapping(value = "/{id}/analysis", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCompanyAnalysis(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Object authUser = request.getAttribute("authUser");
        if (authUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long userId = Long.parseLong(authUser.toString().split("\\|")[0]);

        return companyService.findByIdAndUser(id, userId)
                .map(c -> c.getAnalysisJson() == null
                        ? ResponseEntity.noContent().build()
                        : ResponseEntity.ok(c.getAnalysisJson()))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body("Company not found"));
    }

    /* ===============================
       🔥 SAVE NORMALIZED ANALYSIS
       =============================== */
    @PostMapping("/{id}/analysis/normalized")
    public ResponseEntity<ApiResponse<Object>> saveNormalizedAnalysis(
            @PathVariable Long id,
            @RequestBody CompanyAnalysisRequest req,
            HttpServletRequest request
    ) {
        Object authUser = request.getAttribute("authUser");
        if (authUser == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "Unauthorized", null));
        }

        Long userId = Long.parseLong(authUser.toString().split("\\|")[0]);

        FinancialSpreading saved =
                companyService.saveNormalizedSpreading(id, userId, req);

        Map<String, Object> resp = new HashMap<>();
        resp.put("spreadingId", saved.getId());
        resp.put("companyId", id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Normalized spreading saved", resp)
        );
    }

    /* ===============================
       MAPPER
       =============================== */
    private CompanyDetailsDTO mapToDetails(Company c) {
        CompanyDetailsDTO d = new CompanyDetailsDTO();

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

        if (c.getOwnerAdditionalSupport() != null) {
            d.setOwnerPersonalNetWorth(
                    c.getOwnerAdditionalSupport().getOwnerPersonalNetWorth()
            );
        }

        if (c.getAccountStatus() != null) {
            d.setYearInBusiness(c.getAccountStatus().getYearInBusiness());
            d.setLocationOfBusiness(c.getAccountStatus().getLocationOfBusiness());
            d.setRelationshipAge(c.getAccountStatus().getRelationshipAge());
            d.setAuditorQuality(c.getAccountStatus().getAuditorQuality());
            d.setAuditorsOpinion(c.getAccountStatus().getAuditorsOpinion());
            d.setNationalizationScheme(c.getAccountStatus().getNationalizationScheme());
        }

        if (c.getAccountConduct() != null) {
            d.setBounceCheques(c.getAccountConduct().getBounceCheques());
            d.setOngoingCreditRelationship(
                    c.getAccountConduct().getOngoingCreditRelationship()
            );
            d.setDelayInReceiptOfPrincipalProfitInstallments(
                    c.getAccountConduct().getDelayInReceiptOfPrincipalProfitInstallments()
            );
            d.setNumberOfDelinquencyInHistory(
                    c.getAccountConduct().getNumberOfDelinquencyInHistory()
            );
            d.setWriteOff(c.getAccountConduct().getWriteOff());
            d.setFraudAndLitigationIncidences(
                    c.getAccountConduct().getFraudAndLitigationIncidences()
            );
        }

        return d;
    }
}
