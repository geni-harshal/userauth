package com.example.userauth.dto;

public class CompanyResponse {
    private Long id;
    private String borrowerId;
    private String companyName;

    public CompanyResponse(Long id, String borrowerId, String companyName) {
        this.id = id; this.borrowerId = borrowerId; this.companyName = companyName;
    }
    public Long getId() { return id; }
    public String getBorrowerId() { return borrowerId; }
    public String getCompanyName() { return companyName; }
}
