package com.example.userauth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "owner_additional_support")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerAdditionalSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // simple field as you described
    @Column(name = "owner_personal_net_worth")
    private String ownerPersonalNetWorth;
}
