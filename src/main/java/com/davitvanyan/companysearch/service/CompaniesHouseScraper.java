package com.davitvanyan.companysearch.service;

import com.davitvanyan.companysearch.config.AppConfig;
import com.davitvanyan.companysearch.model.entity.Company;
import com.davitvanyan.companysearch.model.entity.Officer;
import com.davitvanyan.companysearch.model.entity.PersonWithSignificantControl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CompaniesHouseScraper {

    private static final Logger log = LoggerFactory.getLogger(CompaniesHouseScraper.class);
    private static final String BASE_URL = "https://find-and-update.company-information.service.gov.uk";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("d MMMM yyyy", Locale.ENGLISH);

    private final AppConfig config;

    public CompaniesHouseScraper(AppConfig config) {
        this.config = config;
    }

    // Entry point — pages through search results until we hit maxCompanies or run out
    public List<Company> scrape(String query) {
        List<Company> results = new ArrayList<>();
        int page = 1;

        while (results.size() < config.maxCompanies) {
            log.info("Fetching search results page {} for query '{}'", page, query);
            List<String> companyPaths = fetchSearchResultLinks(query, page);
            if (companyPaths.isEmpty()) break;

            for (String path : companyPaths) {
                if (results.size() >= config.maxCompanies) break;
                Company company = scrapeCompany(path);
                if (company != null) results.add(company);
                sleep();
            }
            page++;
        }

        log.info("Scraped {} companies for query '{}'", results.size(), query);
        return results;
    }

    // Fetches one search results page and returns the /company/XXXXXXXX paths
    // Based on real HTML: <li class="type-company"><h3><a href="/company/14345132">
    private List<String> fetchSearchResultLinks(String query, int page) {
        String url = BASE_URL + "/search?q=" + encode(query) + "&page=" + page;
        List<String> paths = new ArrayList<>();

        try {
            Document doc = fetch(url);

            // Select only company results, not officer results
            // Real HTML: <li class="type-company"> contains <a href="/company/NUMBER">
            Elements companyItems = doc.select("li.type-company h3 a[href^=/company/]");

            for (Element a : companyItems) {
                String href = a.attr("href").trim();
                // href is exactly /company/XXXXXXXX — no sub-pages at this point
                if (!paths.contains(href)) {
                    paths.add(href);
                }
            }

            log.info("Found {} company links on page {}", paths.size(), page);
        } catch (IOException e) {
            log.error("Failed to fetch search page {} for '{}': {}", page, query, e.getMessage());
        }

        return paths;
    }

    // Scrapes overview + officers + PSC for one company
    private Company scrapeCompany(String path) {
        String companyNumber = path.replace("/company/", "").replace("/", "");
        log.info("Scraping company {}", companyNumber);

        Company company = new Company();
        company.setCompanyNumber(companyNumber);

        try {
            scrapeOverview(company, path);
            sleep();
            scrapeOfficers(company, path);
            sleep();
            scrapePsc(company, path);
        } catch (IOException e) {
            log.error("Failed to scrape company {}: {}", companyNumber, e.getMessage());
            // return null so the caller skips this company rather than saving incomplete data
            return null;
        }

        return company;
    }

    // Company overview page: /company/XXXXXXXX
    // The page uses <dl> definition lists for structured data
    private void scrapeOverview(Company company, String path) throws IOException {
        Document doc = fetch(BASE_URL + path);

        // Company name is in <h1 class="heading-xlarge"> or plain <h1>
        Element h1 = doc.selectFirst("h1.heading-xlarge");
        if (h1 == null) h1 = doc.selectFirst("h1");
        if (h1 != null) company.setName(h1.text().trim());

        // Registered office address is in a specific section
        // The address section has id="registered-office-address"
        Element addressSection = doc.selectFirst("#registered-office-address");
        if (addressSection != null) {
            Element addressEl = addressSection.selectFirst("p.govuk-body");
            if (addressEl != null) company.setAddress(addressEl.text().trim());
        }

        // Status, type, dates are in <dl> definition lists throughout the page
        // Each <dt> label is followed by a <dd> value
        company.setStatus(extractDefinition(doc, "Company status"));
        company.setCompanyType(extractDefinition(doc, "Company type"));
        company.setIncorporatedOn(parseDate(extractDefinition(doc, "Incorporated on")));
    }

    // Officers page: /company/XXXXXXXX/officers
    // Each officer is in a <div class="appointment-1"> or similar
    private void scrapeOfficers(Company company, String path) throws IOException {
        Document doc = fetch(BASE_URL + path + "/officers");

        // Officers are listed in <div class="appointment-1">, <div class="appointment-2">, etc.
        Elements appointments = doc.select("div[class^=appointment-]");

        for (Element appointment : appointments) {
            Officer officer = new Officer();

            // Officer name: <span id="officer-name-1"> or similar
            Element nameEl = appointment.selectFirst("span[id^=officer-name-]");
            if (nameEl != null) officer.setName(nameEl.text().trim());

            // Role and date are in <dd> following their <dt> labels
            officer.setRole(extractDefinition(appointment, "Role"));
            officer.setAppointedOn(parseDate(extractDefinition(appointment, "Appointed on")));

            officer.setCompany(company);
            company.getOfficers().add(officer);
        }
    }

    // PSC page: /company/XXXXXXXX/persons-with-significant-control
    private void scrapePsc(Company company, String path) throws IOException {
        Document doc = fetch(BASE_URL + path + "/persons-with-significant-control");

        Elements appointments = doc.select("div[class^=appointment-]");

        for (Element appointment : appointments) {
            PersonWithSignificantControl psc = new PersonWithSignificantControl();

            // PSC name: <span id="psc-name-1"> or similar
            Element nameEl = appointment.selectFirst("span[id^=psc-name-]");
            if (nameEl != null) psc.setName(nameEl.text().trim());

            // Nature of control is a list of <li> items
            Elements nocItems = appointment.select("li");
            if (!nocItems.isEmpty()) {
                String noc = nocItems.stream()
                        .map(Element::text)
                        .reduce((a, b) -> a + "; " + b)
                        .orElse("");
                psc.setNatureOfControl(noc);
            }

            psc.setCompany(company);
            company.getPersonsWithSignificantControl().add(psc);
        }
    }


    private String extractDefinition(Element parent, String label) {
        for (Element dt : parent.select("dt")) {
            if (dt.text().trim().equalsIgnoreCase(label)) {
                Element dd = dt.nextElementSibling();
                if (dd != null && dd.tagName().equals("dd")) {
                    return dd.text().trim();
                }
            }
        }
        return null;
    }

    private Document fetch(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(config.userAgent)
                .timeout(10_000)
                .get();
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return LocalDate.parse(raw.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            log.warn("Could not parse date: '{}'", raw);
            return null;
        }
    }

    private String encode(String query) {
        return query.replace(" ", "+");
    }

    private void sleep() {
        try {
            Thread.sleep(config.delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}