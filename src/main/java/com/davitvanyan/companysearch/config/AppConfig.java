package com.davitvanyan.companysearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${scraper.user-agent}")
    public String userAgent;

    @Value("${scraper.delay-ms}")
    public int delayMs;

    @Value("${scraper.max-companies}")
    public int maxCompanies;

    @Value("${cache.ttl-hours}")
    public int cacheTtlHours;
}