package com.davitvanyan.companysearch.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "company")
@Getter @Setter @NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_query_id", nullable = false)
    private SearchQuery searchQuery;

    @Column(name = "company_number")
    private String companyNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private String status;

    @Column(name = "company_type")
    private String companyType;

    @Column(name = "incorporated_on")
    private LocalDate incorporatedOn;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Officer> officers = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonWithSignificantControl> personsWithSignificantControl = new ArrayList<>();
}