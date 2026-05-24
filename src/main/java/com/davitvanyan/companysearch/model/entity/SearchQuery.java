package com.davitvanyan.companysearch.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "search_query")
@Getter @Setter @NoArgsConstructor
public class SearchQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "query_term", nullable = false, unique = true)
    private String queryTerm;

    @Column(name = "searched_at", nullable = false)
    private LocalDateTime searchedAt;

    @OneToMany(mappedBy = "searchQuery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Company> companies = new ArrayList<>();

    public SearchQuery(String queryTerm, LocalDateTime searchedAt) {
        this.queryTerm = queryTerm;
        this.searchedAt = searchedAt;
    }
}