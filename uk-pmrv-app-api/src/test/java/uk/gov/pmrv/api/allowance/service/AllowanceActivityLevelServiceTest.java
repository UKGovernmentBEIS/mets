package uk.gov.pmrv.api.allowance.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.AllowanceActivityLevelEntity;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.allowance.repository.AllowanceActivityLevelRepository;
import uk.gov.pmrv.api.allowance.validation.AllowanceActivityLevelValidator;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AllowanceActivityLevelServiceTest {

    @InjectMocks
    private AllowanceActivityLevelService service;

    @Mock
    private AllowanceActivityLevelRepository allowanceActivityLevelRepository;

    @Mock
    private AllowanceActivityLevelValidator allowanceActivityLevelValidator;

    @Test
    void submitActivityLevels() {
        final Year currentYear = Year.now();
        final Long accountId = 1L;
        final List<ActivityLevel> activityLevels = List.of(
                ActivityLevel.builder()
                        .year(currentYear)
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .changeType(ChangeType.DECREASE)
                        .changedActivityLevel("-1%")
                        .comments("Comments")
                        .build(),
                ActivityLevel.builder()
                        .year(currentYear)
                        .subInstallationName(SubInstallationName.ADIPIC_ACID)
                        .changeType(ChangeType.OTHER)
                        .otherChangeTypeName("Other name")
                        .changedActivityLevel("-1%")
                        .comments("Comments")
                        .build()
        );

        AllowanceActivityLevelEntity firstEntity = AllowanceActivityLevelEntity.builder()
                .accountId(accountId)
                .year(currentYear)
                .subInstallationName(SubInstallationName.ALUMINIUM)
                .changeType(ChangeType.DECREASE)
                .amount("-1%")
                .comments("Comments")
                .build();
        AllowanceActivityLevelEntity secondEntity = AllowanceActivityLevelEntity.builder()
                .accountId(accountId)
                .year(currentYear)
                .subInstallationName(SubInstallationName.ADIPIC_ACID)
                .changeType(ChangeType.OTHER)
                .otherChangeTypeName("Other name")
                .amount("-1%")
                .comments("Comments")
                .build();

        // Invoke
        service.submitActivityLevels(activityLevels, accountId);

        // Verify
        verify(allowanceActivityLevelValidator, times(1)).validate(activityLevels);

        ArgumentCaptor<List<AllowanceActivityLevelEntity>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(allowanceActivityLevelRepository, times(1)).saveAll(listCaptor.capture());

        List<AllowanceActivityLevelEntity> inserted = listCaptor.getValue();
        assertThat(inserted).hasSize(2);

        AllowanceActivityLevelEntity firstInserted = inserted.get(0);
        assertThat(firstInserted.getAccountId()).isEqualTo(firstEntity.getAccountId());
        assertThat(firstInserted.getYear()).isEqualTo(firstEntity.getYear());
        assertThat(firstInserted.getSubInstallationName()).isEqualTo(firstEntity.getSubInstallationName());
        assertThat(firstInserted.getChangeType()).isEqualTo(firstEntity.getChangeType());
        assertThat(firstInserted.getAmount()).isEqualTo(firstEntity.getAmount());
        assertThat(firstInserted.getComments()).isEqualTo(firstEntity.getComments());
        assertThat(firstInserted.getCreationDate()).isNotNull();

        AllowanceActivityLevelEntity secondInserted = inserted.get(1);
        assertThat(secondInserted.getAccountId()).isEqualTo(secondEntity.getAccountId());
        assertThat(secondInserted.getYear()).isEqualTo(secondEntity.getYear());
        assertThat(secondInserted.getSubInstallationName()).isEqualTo(secondEntity.getSubInstallationName());
        assertThat(secondInserted.getChangeType()).isEqualTo(secondEntity.getChangeType());
        assertThat(secondInserted.getAmount()).isEqualTo(secondEntity.getAmount());
        assertThat(secondInserted.getComments()).isEqualTo(secondEntity.getComments());
        assertThat(secondInserted.getCreationDate()).isNotNull();
    }
}
