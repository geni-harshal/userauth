package com.example.userauth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year_in_business")
    private String yearInBusiness;

    @Column(name = "location_of_business")
    private String locationOfBusiness;

    @Column(name = "relationship_age")
    private String relationshipAge;

    @Column(name = "auditor_quality")
    private String auditorQuality;

    @Column(name = "auditors_opinion")
    private String auditorsOpinion;

    @Column(name = "nationalization_scheme")
    private String nationalizationScheme;
}
