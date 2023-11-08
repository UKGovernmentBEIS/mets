package uk.gov.pmrv.api.allowance.service;

import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.mapper.AllowanceMapper;
import uk.gov.pmrv.api.allowance.repository.AllowanceActivityLevelRepository;
import uk.gov.pmrv.api.allowance.repository.AllowanceAllocationRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllowanceQueryService {

    private final AllowanceAllocationRepository allowanceAllocationRepository;
    private final AllowanceActivityLevelRepository allowanceActivityLevelRepository;
    private static final AllowanceMapper ALLOWANCE_MAPPER = Mappers.getMapper(AllowanceMapper.class);

    public List<HistoricalActivityLevel> getHistoricalActivityLevelsByAccount(Long accountId) {
        return allowanceActivityLevelRepository.findAllByAccountId(accountId).stream()
                .map(ALLOWANCE_MAPPER::toHistoricalActivityLevel)
                .collect(Collectors.toList());
    }

    public Set<PreliminaryAllocation> getPreliminaryAllocationsByAccount(Long accountId) {
        return allowanceAllocationRepository.findAllByAccountId(accountId).stream()
                .map(ALLOWANCE_MAPPER::toPreliminaryAllocation)
                .collect(Collectors.toSet());
    }
}
