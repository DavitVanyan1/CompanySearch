package com.davitvanyan.companysearch.controller;

import com.davitvanyan.companysearch.model.dto.CompanyResponse;
import com.davitvanyan.companysearch.model.dto.OfficerDto;
import com.davitvanyan.companysearch.model.dto.PscDto;
import com.davitvanyan.companysearch.model.entity.Company;
import com.davitvanyan.companysearch.service.CompanySearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CompanySearchController {

    private final CompanySearchService searchService;

    public CompanySearchController(CompanySearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<CompanyResponse>> search(@RequestParam String q) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        List<Company> companies = searchService.search(q);
        List<CompanyResponse> response = companies.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    private CompanyResponse toResponse(Company company) {
        List<OfficerDto> officers = company.getOfficers().stream()
                .map(o -> new OfficerDto(o.getName(), o.getRole(), o.getAppointedOn()))
                .toList();

        List<PscDto> pscs = company.getPersonsWithSignificantControl().stream()
                .map(p -> new PscDto(p.getName(), p.getNatureOfControl()))
                .toList();

        return new CompanyResponse(
                company.getCompanyNumber(),
                company.getName(),
                company.getStatus(),
                company.getCompanyType(),
                company.getIncorporatedOn(),
                company.getAddress(),
                officers,
                pscs
        );
    }
}