package com.example.userauth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "spreading_year")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpreadingYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. "Evaluation Year (-1)" or "2018" depending on your mapping keys
    private String label;

    // actual year number (if parseable)
    private Integer yearNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spreading_id")
    private FinancialSpreading spreading;
}
