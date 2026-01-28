package com.example.userauth.service;

import com.example.userauth.dto.CompanyCreateRequest;
import com.example.userauth.entity.*;
import com.example.userauth.exception.ApiException;
import com.example.userauth.repository.CompanyRepository;
import com.example.userauth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public List<Company> listAll() {
    return companyRepository.findAll();
    }

    public CompanyService(CompanyRepository companyRepository,
                          UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Company createCompany(CompanyCreateRequest req, Long creatorUserId) {

        // Ensure creator exists
        var userOpt = userRepository.findById(creatorUserId);
        if (userOpt.isEmpty()) throw new ApiException("Creator user not found");

        User creator = userOpt.get();

        Company company = new Company();
        company.setCompanyName(req.getCompanyName());
        company.setCurrency(req.getCurrency());
        company.setBorrowerClassification(req.getBorrowerClassification());
        company.setDateOfAssessment(req.getDateOfAssessment());
        company.setRelationshipManager(req.getRelationshipManager());
        company.setIndustry(req.getIndustry());
        company.setCreatedBy(creator);

        // map qualitative inputs (if present)
        if (req.getOwnerAdditionalSupport() != null) {
            OwnerAdditionalSupport os = OwnerAdditionalSupport.builder()
                    .ownerPersonalNetWorth(req.getOwnerAdditionalSupport().getOwnerPersonalNetWorth())
                    .build();
            company.setOwnerAdditionalSupport(os);
        }

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

        if (req.getAccountConduct() != null) {
            CompanyCreateRequest.AccountConductDTO ac = req.getAccountConduct();
            AccountConduct conduct = AccountConduct.builder()
                    .bounceCheques(ac.getBounceCheques())
                    .ongoingCreditRelationship(ac.getOngoingCreditRelationship())
                    .delayInReceiptOfPrincipalProfitInstallments(ac.getDelayInReceiptOfPrincipalProfitInstallments())
                    .numberOfDelinquencyInHistory(ac.getNumberOfDelinquencyInHistory())
                    .writeOff(ac.getWriteOff())
                    .fraudAndLitigationIncidences(ac.getFraudAndLitigationIncidences())
                    .build();
            company.setAccountConduct(conduct);
        }

        // Save company first to get DB-generated id
        Company saved = companyRepository.save(company);

        // Generate borrowerId (format BRW000001)
        String borrowerId = String.format("BRW%06d", saved.getId());
        saved.setBorrowerId(borrowerId);

        // Save again to persist borrowerId
        return companyRepository.save(saved);
    }

    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }
}
