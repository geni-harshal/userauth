package com.example.userauth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "spreading_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpreadingCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // top-level category name, e.g., "Current Assets"
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spreading_id")
    private FinancialSpreading spreading;

    // items under this category (leaf rows like "Cash and bank balances")
    @Builder.Default
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpreadingItem> items = new ArrayList<>();

}
