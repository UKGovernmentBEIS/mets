package uk.gov.pmrv.api.allowance.service;

import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.allowance.domain.AllowanceAllocationEntity;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.mapper.AllowanceMapper;
import uk.gov.pmrv.api.allowance.repository.AllowanceAllocationRepository;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class AllowanceAllocationService {

    private final AllowanceAllocationRepository allowanceAllocationRepository;
    private final AllowanceAllocationValidator allowanceAllocationValidator;
    private static final AllowanceMapper ALLOWANCE_MAPPER = Mappers.getMapper(AllowanceMapper.class);

    @Transactional
    public void submitAllocations(Set<PreliminaryAllocation> allocations, Long accountId) {
        // Validate
        boolean isValid = allowanceAllocationValidator.isValid(allocations);

        if(!isValid) {
            throw new BusinessException(ErrorCode.INVALID_ALLOWANCE_ALLOCATIONS);
        }

        Set<AllowanceAllocationEntity> entities = allowanceAllocationRepository.findAllByAccountId(accountId);

        // Delete
        List<AllowanceAllocationEntity> deletedEntities = entities.stream()
                .filter(entity -> allocations.stream().noneMatch(filterPreliminaryAllocations(entity)))
                .toList();
        allowanceAllocationRepository.deleteAll(deletedEntities);
        deletedEntities.forEach(entities::remove);

        // Update
        entities.forEach(entity -> allocations.stream()
                .filter(filterPreliminaryAllocations(entity)).findFirst()
                .ifPresent(allocation -> entity.setAllocation(allocation.getAllowances()))
        );

        // Insert
        List<AllowanceAllocationEntity> newEntities = allocations.stream()
                .filter(item -> entities.stream().noneMatch(filterEntities(item)))
                .map(item -> ALLOWANCE_MAPPER.toAllowanceAllocationEntity(item, accountId))
                .toList();
        entities.addAll(newEntities);

        // Save all
        allowanceAllocationRepository.saveAll(entities);
    }

    private Predicate<PreliminaryAllocation> filterPreliminaryAllocations(AllowanceAllocationEntity entity) {
        return item -> item.getSubInstallationName().equals(entity.getSubInstallationName())
                && item.getYear().equals(entity.getYear());
    }

    private Predicate<AllowanceAllocationEntity> filterEntities(PreliminaryAllocation allocation) {
        return entity -> entity.getSubInstallationName().equals(allocation.getSubInstallationName())
                && entity.getYear().equals(allocation.getYear());
    }
}
