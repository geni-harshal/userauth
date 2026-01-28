package com.example.userauth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account_conduct")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountConduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bounce_cheques")
    private String bounceCheques;

    @Column(name = "ongoing_credit_relationship")
    private String ongoingCreditRelationship;

    @Column(name = "delay_in_receipt")
    private String delayInReceiptOfPrincipalProfitInstallments;

    @Column(name = "num_of_delinquency_history")
    private String numberOfDelinquencyInHistory;

    @Column(name = "write_off")
    private String writeOff;

    @Column(name = "fraud_litigation")
    private String fraudAndLitigationIncidences;
}
