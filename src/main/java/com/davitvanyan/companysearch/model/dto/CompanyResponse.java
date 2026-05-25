package com.davitvanyan.companysearch.model.dto;

import java.time.LocalDate;
import java.util.List;

public record CompanyResponse(
        String companyNumber,
        String name,
        String status,
        String companyType,
        LocalDate incorporatedOn,
        String address,
        List<OfficerDto> officers,
        List<PscDto> personsWithSignificantControl
) {}