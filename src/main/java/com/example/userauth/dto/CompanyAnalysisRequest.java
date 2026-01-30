package com.example.userauth.dto;

import java.util.Map;

public class CompanyAnalysisRequest {
    private Long companyId;
    private Map<String, String> yearMapping;  // Changed from Map<String, Object>
    private Map<String, Map<String, Map<String, String>>> yearWiseData; // New structure

    // Getters and setters
    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Map<String, String> getYearMapping() {
        return yearMapping;
    }

    public void setYearMapping(Map<String, String> yearMapping) {
        this.yearMapping = yearMapping;
    }

    public Map<String, Map<String, Map<String, String>>> getYearWiseData() {
        return yearWiseData;
    }

    public void setYearWiseData(Map<String, Map<String, Map<String, String>>> yearWiseData) {
        this.yearWiseData = yearWiseData;
    }
}