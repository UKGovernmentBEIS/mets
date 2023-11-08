package uk.gov.pmrv.api.allowance.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.allowance.domain.AllowanceActivityLevelEntity;
import uk.gov.pmrv.api.allowance.domain.AllowanceAllocationEntity;
import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.allowance.repository.AllowanceActivityLevelRepository;
import uk.gov.pmrv.api.allowance.repository.AllowanceAllocationRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllowanceQueryServiceTest {

    @InjectMocks
    private AllowanceQueryService service;

    @Mock
    private AllowanceAllocationRepository allowanceAllocationRepository;

    @Mock
    private AllowanceActivityLevelRepository allowanceActivityLevelRepository;

    @Test
    void getHistoricalActivityLevelsByAccount() {
        final Long accountId = 1L;
        final LocalDateTime creationDate = LocalDateTime.now();

        final List<AllowanceActivityLevelEntity> entities = List.of(
                AllowanceActivityLevelEntity.builder()
                        .accountId(accountId)
                        .year(Year.of(2020))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .changeType(ChangeType.DECREASE)
                        .amount("-1%")
                        .comments("Comments")
                        .creationDate(creationDate)
                        .build(),
                AllowanceActivityLevelEntity.builder()
                        .accountId(accountId)
                        .year(Year.of(2020))
                        .subInstallationName(SubInstallationName.ADIPIC_ACID)
                        .changeType(ChangeType.OTHER)
                        .otherChangeTypeName("Other name")
                        .amount("-1%")
                        .comments("Comments")
                        .creationDate(creationDate)
                        .build()
        );

        List<HistoricalActivityLevel> expected = List.of(
                HistoricalActivityLevel.builder()
                        .year(Year.of(2020))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .changeType(ChangeType.DECREASE)
                        .changedActivityLevel("-1%")
                        .comments("Comments")
                        .creationDate(creationDate)
                        .build(),
                HistoricalActivityLevel.builder()
                        .year(Year.of(2020))
                        .subInstallationName(SubInstallationName.ADIPIC_ACID)
                        .changeType(ChangeType.OTHER)
                        .otherChangeTypeName("Other name")
                        .changedActivityLevel("-1%")
                        .comments("Comments")
                        .creationDate(creationDate)
                        .build()
        );

        when(allowanceActivityLevelRepository.findAllByAccountId(accountId)).thenReturn(entities);

        // Invoke
        List<HistoricalActivityLevel> actual = service.getHistoricalActivityLevelsByAccount(accountId);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(allowanceActivityLevelRepository, times(1)).findAllByAccountId(accountId);
    }

    @Test
    void getPreliminaryAllocationsByAccount() {
        final Long accountId = 1L;

        final Set<AllowanceAllocationEntity> entities = Set.of(
                AllowanceAllocationEntity.builder()
                        .id(1L)
                        .accountId(accountId)
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2020))
                        .allocation(10)
                        .build(),
                AllowanceAllocationEntity.builder()
                        .id(1L)
                        .accountId(accountId)
                        .subInstallationName(SubInstallationName.CARBON_BLACK)
                        .year(Year.of(2020))
                        .allocation(20)
                        .build()
        );
        final Set<PreliminaryAllocation> expected = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2020))
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.CARBON_BLACK)
                        .year(Year.of(2020))
                        .allowances(20)
                        .build()
        );

        when(allowanceAllocationRepository.findAllByAccountId(accountId)).thenReturn(entities);

        // Invoke
        Set<PreliminaryAllocation> actual = service.getPreliminaryAllocationsByAccount(accountId);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(allowanceAllocationRepository, times(1)).findAllByAccountId(accountId);
    }
}
