package com.davitvanyan.companysearch.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "person_with_significant_control")
@Getter @Setter @NoArgsConstructor
public class PersonWithSignificantControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "name")
    private String name;

    @Column(name = "nature_of_control", columnDefinition = "TEXT")
    private String natureOfControl;
}