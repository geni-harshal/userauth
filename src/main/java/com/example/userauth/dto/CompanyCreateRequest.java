package com.example.userauth.dto;

import java.time.LocalDate;

public class CompanyCreateRequest {

    private String companyName;
    private String currency;
    private String borrowerClassification;
    private LocalDate dateOfAssessment;
    private String relationshipManager;
    private String industry;

    // Qualitative inputs
    private OwnerAdditionalSupportDTO ownerAdditionalSupport;
    private AccountStatusDTO accountStatus;
    private AccountConductDTO accountConduct;

    // getters/setters

    // OwnerAdditionalSupportDTO
    public static class OwnerAdditionalSupportDTO {
        private String ownerPersonalNetWorth;
        // getters/setters
        public String getOwnerPersonalNetWorth() { return ownerPersonalNetWorth; }
        public void setOwnerPersonalNetWorth(String v) { ownerPersonalNetWorth = v; }
    }

    // AccountStatusDTO
    public static class AccountStatusDTO {
        private String yearInBusiness;
        private String locationOfBusiness;
        private String relationshipAge;
        private String auditorQuality;
        private String auditorsOpinion;
        private String nationalizationScheme;
        // getters/setters below...
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
    }

    // AccountConductDTO
    public static class AccountConductDTO {
        private String bounceCheques;
        private String ongoingCreditRelationship;
        private String delayInReceiptOfPrincipalProfitInstallments;
        private String numberOfDelinquencyInHistory;
        private String writeOff;
        private String fraudAndLitigationIncidences;
        // getters/setters...
        public String getBounceCheques() { return bounceCheques; }
        public void setBounceCheques(String bounceCheques) { this.bounceCheques = bounceCheques; }
        public String getOngoingCreditRelationship() { return ongoingCreditRelationship; }
        public void setOngoingCreditRelationship(String ongoingCreditRelationship) { this.ongoingCreditRelationship = ongoingCreditRelationship; }
        public String getDelayInReceiptOfPrincipalProfitInstallments() { return delayInReceiptOfPrincipalProfitInstallments; }
        public void setDelayInReceiptOfPrincipalProfitInstallments(String v) { this.delayInReceiptOfPrincipalProfitInstallments = v; }
        public String getNumberOfDelinquencyInHistory() { return numberOfDelinquencyInHistory; }
        public void setNumberOfDelinquencyInHistory(String v) { this.numberOfDelinquencyInHistory = v; }
        public String getWriteOff() { return writeOff; }
        public void setWriteOff(String writeOff) { this.writeOff = writeOff; }
        public String getFraudAndLitigationIncidences() { return fraudAndLitigationIncidences; }
        public void setFraudAndLitigationIncidences(String v) { this.fraudAndLitigationIncidences = v; }
    }

    // Getters & setters for top-level fields:
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

    public OwnerAdditionalSupportDTO getOwnerAdditionalSupport() { return ownerAdditionalSupport; }
    public void setOwnerAdditionalSupport(OwnerAdditionalSupportDTO ownerAdditionalSupport) { this.ownerAdditionalSupport = ownerAdditionalSupport; }
    public AccountStatusDTO getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatusDTO accountStatus) { this.accountStatus = accountStatus; }
    public AccountConductDTO getAccountConduct() { return accountConduct; }
    public void setAccountConduct(AccountConductDTO accountConduct) { this.accountConduct = accountConduct; }
}
