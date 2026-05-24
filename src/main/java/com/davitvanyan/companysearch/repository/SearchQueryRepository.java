package com.davitvanyan.companysearch.repository;

import com.davitvanyan.companysearch.model.entity.SearchQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchQueryRepository extends JpaRepository<SearchQuery, Long> {

    Optional<SearchQuery> findByQueryTerm(String queryTerm);
}