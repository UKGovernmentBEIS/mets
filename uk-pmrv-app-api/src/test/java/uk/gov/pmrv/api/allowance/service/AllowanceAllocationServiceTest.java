package uk.gov.pmrv.api.allowance.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.allowance.domain.AllowanceAllocationEntity;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.allowance.repository.AllowanceAllocationRepository;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllowanceAllocationServiceTest {

    @InjectMocks
    private AllowanceAllocationService service;

    @Mock
    private AllowanceAllocationRepository allowanceAllocationRepository;

    @Mock
    private AllowanceAllocationValidator allowanceAllocationValidator;

    @Test
    void submitAllocations() {
        final Year currentYear = Year.now();
        final Long accountId = 1L;
        final Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2000))
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(currentYear)
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(currentYear)
                        .allowances(20)
                        .build()
        );
        Set<AllowanceAllocationEntity> entities = new HashSet<>();
        entities.add(AllowanceAllocationEntity.builder()
                .id(1L)
                .accountId(accountId)
                .subInstallationName(SubInstallationName.ALUMINIUM)
                .year(currentYear)
                .allocation(100)
                .build());
        entities.add(AllowanceAllocationEntity.builder()
                .id(2L)
                .accountId(accountId)
                .subInstallationName(SubInstallationName.ALUMINIUM)
                .year(Year.of(2000))
                .allocation(100)
                .build());
        AllowanceAllocationEntity deletedEntity = AllowanceAllocationEntity.builder()
                .id(3L)
                .accountId(accountId)
                .subInstallationName(SubInstallationName.COKE)
                .year(currentYear)
                .allocation(200)
                .build();
        entities.add(deletedEntity);

        final Set<AllowanceAllocationEntity> updatedEntities = Set.of(
                AllowanceAllocationEntity.builder()
                        .id(1L)
                        .accountId(accountId)
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(currentYear)
                        .allocation(10)
                        .build(),
                AllowanceAllocationEntity.builder()
                        .id(2L)
                        .accountId(accountId)
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2000))
                        .allocation(10)
                        .build(),
                AllowanceAllocationEntity.builder()
                        .id(null)
                        .accountId(accountId)
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(currentYear)
                        .allocation(20)
                        .build()
        );

        when(allowanceAllocationValidator.isValid(allocations)).thenReturn(true);
        when(allowanceAllocationRepository.findAllByAccountId(accountId)).thenReturn(entities);

        // Invoke
        service.submitAllocations(allocations, accountId);

        // Verify
        verify(allowanceAllocationValidator, times(1)).isValid(allocations);
        verify(allowanceAllocationRepository, times(1)).findAllByAccountId(accountId);
        verify(allowanceAllocationRepository, times(1)).deleteAll(List.of(deletedEntity));
        verify(allowanceAllocationRepository, times(1)).saveAll(updatedEntities);
    }

    @Test
    void submitAllocations_invalid() {
        final Year currentYear = Year.now();
        final Long accountId = 1L;
        final Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(currentYear)
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(currentYear)
                        .allowances(20)
                        .build()
        );

        when(allowanceAllocationValidator.isValid(allocations)).thenReturn(false);

        // Invoke
        final BusinessException businessException = assertThrows(
                BusinessException.class, () -> service.submitAllocations(allocations, accountId)
        );

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.INVALID_ALLOWANCE_ALLOCATIONS);
        verify(allowanceAllocationValidator, times(1)).isValid(allocations);
        verifyNoInteractions(allowanceAllocationRepository);
    }
}
