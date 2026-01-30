package com.example.userauth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "financial_spreading")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialSpreading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // owner company
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // who uploaded / created
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    // year mapping list
@Builder.Default
@OneToMany(mappedBy = "spreading", cascade = CascadeType.ALL, orphanRemoval = true)
private List<SpreadingYear> years = new ArrayList<>();

@Builder.Default
@OneToMany(mappedBy = "spreading", cascade = CascadeType.ALL, orphanRemoval = true)
private List<SpreadingCategory> categories = new ArrayList<>();

}
