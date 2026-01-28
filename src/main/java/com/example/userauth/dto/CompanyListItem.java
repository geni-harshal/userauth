package com.example.userauth.dto;

public class CompanyListItem {
    private Long id;
    private String borrowerId;
    private String companyName;
    private String industry;
    private String currency;

    public CompanyListItem(Long id, String borrowerId, String companyName, String industry, String currency) {
        this.id = id; this.borrowerId = borrowerId; this.companyName = companyName;
        this.industry = industry; this.currency = currency;
    }

    public Long getId() { return id; }
    public String getBorrowerId() { return borrowerId; }
    public String getCompanyName() { return companyName; }
    public String getIndustry() { return industry; }
    public String getCurrency() { return currency; }
}
