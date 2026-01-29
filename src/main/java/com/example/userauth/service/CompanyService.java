package com.example.userauth.service;

import com.example.userauth.dto.CompanyCreateRequest;
import com.example.userauth.entity.*;
import com.example.userauth.exception.ApiException;
import com.example.userauth.repository.CompanyRepository;
import com.example.userauth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository,
                          UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    /* ===============================
       LIST
       =============================== */
    public List<Company> listAll() {
        return companyRepository.findAll();
    }

    /* ===============================
       CREATE
       =============================== */
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

        /* -------- Owner Support -------- */
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

        // Save once to get ID
        Company saved = companyRepository.save(company);

        // Generate borrowerId: BRW000001
        saved.setBorrowerId(String.format("BRW%06d", saved.getId()));

        return companyRepository.save(saved);
    }

    /* ===============================
       FIND BY ID
       =============================== */
    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    /* ===============================
       DELETE (NEW)
       =============================== */
    @Transactional
    public void deleteById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Company not found"));

        // Cascades will delete:
        // - OwnerAdditionalSupport
        // - AccountStatus
        // - AccountConduct
        companyRepository.delete(company);
    }
}
