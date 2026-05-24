package com.davitvanyan.companysearch.repository;

import com.davitvanyan.companysearch.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}