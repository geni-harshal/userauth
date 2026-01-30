package com.example.userauth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "spreading_value")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpreadingValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // optional — helpful to keep label
    private String yearLabel;

    private Integer yearNumber;

    @Column(precision = 19, scale = 4)
    private BigDecimal value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private SpreadingItem item;
}
