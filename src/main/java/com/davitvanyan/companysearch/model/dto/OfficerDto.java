package com.davitvanyan.companysearch.model.dto;

import java.time.LocalDate;

public record OfficerDto(
        String name,
        String role,
        LocalDate appointedOn
) {}