package com.davitvanyan.companysearch.service;

import com.davitvanyan.companysearch.config.AppConfig;
import com.davitvanyan.companysearch.model.entity.Company;
import com.davitvanyan.companysearch.model.entity.SearchQuery;
import com.davitvanyan.companysearch.repository.CompanyRepository;
import com.davitvanyan.companysearch.repository.SearchQueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompanySearchService {

    private static final Logger log = LoggerFactory.getLogger(CompanySearchService.class);

    private final SearchQueryRepository searchQueryRepository;
    private final CompanyRepository companyRepository;
    private final CompaniesHouseScraper scraper;
    private final AppConfig config;

    public CompanySearchService(SearchQueryRepository searchQueryRepository,
                                CompanyRepository companyRepository,
                                CompaniesHouseScraper scraper,
                                AppConfig config) {
        this.searchQueryRepository = searchQueryRepository;
        this.companyRepository = companyRepository;
        this.scraper = scraper;
        this.config = config;
    }

    @Transactional
    public List<Company> search(String query) {
        String normalised = query.trim().toLowerCase();
        Optional<SearchQuery> cached = searchQueryRepository.findByQueryTerm(normalised);

        if (cached.isPresent() && isFresh(cached.get())) {
            log.info("Cache hit for query '{}'", normalised);
            List<Company> companies = cached.get().getCompanies();
            // initialise lazy collections while still inside the transaction
            companies.forEach(c -> {
                c.getOfficers().size();
                c.getPersonsWithSignificantControl().size();
            });
            return companies;
        }

        log.info("Cache miss for query '{}', scraping Companies House", normalised);

        cached.ifPresent(old -> {
            searchQueryRepository.delete(old);
            searchQueryRepository.flush();
        });

        List<Company> companies = scraper.scrape(normalised);

        Optional<SearchQuery> raceCheck = searchQueryRepository.findByQueryTerm(normalised);
        if (raceCheck.isPresent()) {
            return raceCheck.get().getCompanies();
        }

        SearchQuery searchQuery = new SearchQuery(normalised, LocalDateTime.now());
        searchQueryRepository.save(searchQuery);

        for (Company company : companies) {
            company.setSearchQuery(searchQuery);
        }
        companyRepository.saveAll(companies);
        searchQuery.getCompanies().addAll(companies);

        return companies;
    }

    private boolean isFresh(SearchQuery searchQuery) {
        LocalDateTime expiresAt = searchQuery.getSearchedAt().plusHours(config.cacheTtlHours);
        return LocalDateTime.now().isBefore(expiresAt);
    }
}