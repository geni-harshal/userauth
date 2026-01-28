package com.example.userauth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Human-readable borrower id e.g. BRW000001
    @Column(name = "borrower_id", unique = true)
    private String borrowerId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    private String currency;

    private String borrowerClassification;

    private LocalDate dateOfAssessment;

    private String relationshipManager;

    private String industry;

    // Which user (creator/admin) created this company
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    // Qualitative inputs - one-to-one with cascading
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "owner_support_id")
    private OwnerAdditionalSupport ownerAdditionalSupport;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_status_id")
    private AccountStatus accountStatus;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_conduct_id")
    private AccountConduct accountConduct;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
