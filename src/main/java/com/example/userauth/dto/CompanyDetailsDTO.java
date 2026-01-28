package com.example.userauth.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CompanyDetailsDTO {
    // core
    private Long id;
    private String borrowerId;
    private String companyName;
    private String currency;
    private String borrowerClassification;
    private LocalDate dateOfAssessment;
    private String relationshipManager;
    private String industry;
    private Long createdByUserId;
    private String createdByEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // owner additional support
    private String ownerPersonalNetWorth;

    // account status
    private String yearInBusiness;
    private String locationOfBusiness;
    private String relationshipAge;
    private String auditorQuality;
    private String auditorsOpinion;
    private String nationalizationScheme;

    // account conduct
    private String bounceCheques;
    private String ongoingCreditRelationship;
    private String delayInReceiptOfPrincipalProfitInstallments;
    private String numberOfDelinquencyInHistory;
    private String writeOff;
    private String fraudAndLitigationIncidences;

    public CompanyDetailsDTO() {}

    // ---- getters / setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBorrowerId() { return borrowerId; }
    public void setBorrowerId(String borrowerId) { this.borrowerId = borrowerId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getBorrowerClassification() { return borrowerClassification; }
    public void setBorrowerClassification(String borrowerClassification) { this.borrowerClassification = borrowerClassification; }

    public LocalDate getDateOfAssessment() { return dateOfAssessment; }
    public void setDateOfAssessment(LocalDate dateOfAssessment) { this.dateOfAssessment = dateOfAssessment; }

    public String getRelationshipManager() { return relationshipManager; }
    public void setRelationshipManager(String relationshipManager) { this.relationshipManager = relationshipManager; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long createdByUserId) { this.createdByUserId = createdByUserId; }

    public String getCreatedByEmail() { return createdByEmail; }
    public void setCreatedByEmail(String createdByEmail) { this.createdByEmail = createdByEmail; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getOwnerPersonalNetWorth() { return ownerPersonalNetWorth; }
    public void setOwnerPersonalNetWorth(String ownerPersonalNetWorth) { this.ownerPersonalNetWorth = ownerPersonalNetWorth; }

    public String getYearInBusiness() { return yearInBusiness; }
    public void setYearInBusiness(String yearInBusiness) { this.yearInBusiness = yearInBusiness; }

    public String getLocationOfBusiness() { return locationOfBusiness; }
    public void setLocationOfBusiness(String locationOfBusiness) { this.locationOfBusiness = locationOfBusiness; }

    public String getRelationshipAge() { return relationshipAge; }
    public void setRelationshipAge(String relationshipAge) { this.relationshipAge = relationshipAge; }

    public String getAuditorQuality() { return auditorQuality; }
    public void setAuditorQuality(String auditorQuality) { this.auditorQuality = auditorQuality; }

    public String getAuditorsOpinion() { return auditorsOpinion; }
    public void setAuditorsOpinion(String auditorsOpinion) { this.auditorsOpinion = auditorsOpinion; }

    public String getNationalizationScheme() { return nationalizationScheme; }
    public void setNationalizationScheme(String nationalizationScheme) { this.nationalizationScheme = nationalizationScheme; }

    public String getBounceCheques() { return bounceCheques; }
    public void setBounceCheques(String bounceCheques) { this.bounceCheques = bounceCheques; }

    public String getOngoingCreditRelationship() { return ongoingCreditRelationship; }
    public void setOngoingCreditRelationship(String ongoingCreditRelationship) { this.ongoingCreditRelationship = ongoingCreditRelationship; }

    public String getDelayInReceiptOfPrincipalProfitInstallments() { return delayInReceiptOfPrincipalProfitInstallments; }
    public void setDelayInReceiptOfPrincipalProfitInstallments(String delayInReceiptOfPrincipalProfitInstallments) { this.delayInReceiptOfPrincipalProfitInstallments = delayInReceiptOfPrincipalProfitInstallments; }

    public String getNumberOfDelinquencyInHistory() { return numberOfDelinquencyInHistory; }
    public void setNumberOfDelinquencyInHistory(String numberOfDelinquencyInHistory) { this.numberOfDelinquencyInHistory = numberOfDelinquencyInHistory; }

    public String getWriteOff() { return writeOff; }
    public void setWriteOff(String writeOff) { this.writeOff = writeOff; }

    public String getFraudAndLitigationIncidences() { return fraudAndLitigationIncidences; }
    public void setFraudAndLitigationIncidences(String fraudAndLitigationIncidences) { this.fraudAndLitigationIncidences = fraudAndLitigationIncidences; }
}
