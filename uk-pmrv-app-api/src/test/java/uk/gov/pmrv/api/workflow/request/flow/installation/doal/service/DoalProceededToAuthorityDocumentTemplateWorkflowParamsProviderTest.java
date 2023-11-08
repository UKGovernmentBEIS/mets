package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.ActivityLevelChangeInformation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DoalProceededToAuthorityDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private DoalProceededToAuthorityDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.DOAL_SUBMITTED);
    }

    @Test
    void constructParams() {
        final Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(Year.of(2020))
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AMMONIA)
                        .year(Year.of(2020))
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AMMONIA)
                        .year(Year.of(2021))
                        .allowances(100)
                        .build()
        );
        List<ActivityLevel> activityLevels = new ArrayList<>();
        ActivityLevel fourth = ActivityLevel.builder()
                .year(Year.of(2020))
                .subInstallationName(SubInstallationName.DOLIME)
                .changeType(ChangeType.DECREASE)
                .changedActivityLevel("-1%")
                .comments("Comments")
                .build();
        activityLevels.add(fourth);
        ActivityLevel third = ActivityLevel.builder()
                .year(Year.of(2020))
                .subInstallationName(SubInstallationName.DOLIME)
                .changeType(ChangeType.DECREASE)
                .changedActivityLevel("-1%")
                .comments("Comments")
                .build();
        activityLevels.add(third);

        ActivityLevel second = ActivityLevel.builder()
                .year(Year.of(2020))
                .subInstallationName(SubInstallationName.ALUMINIUM)
                .changeType(ChangeType.DECREASE)
                .changedActivityLevel("-1%")
                .comments("Comments")
                .build();
        activityLevels.add(second);

        ActivityLevel first = ActivityLevel.builder()
                .year(Year.of(2019))
                .subInstallationName(SubInstallationName.ALUMINIUM)
                .changeType(ChangeType.DECREASE)
                .changedActivityLevel("-1%")
                .comments("Comments")
                .build();
        activityLevels.add(first);

        final Map<Year, Integer> totalAllocations = Map.of(
                Year.of(2020), 20,
                Year.of(2021), 100
        );

        final DoalRequestPayload payload = DoalRequestPayload.builder()
                .doal(Doal.builder()
                        .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                .activityLevels(activityLevels)
                                .preliminaryAllocations(new TreeSet<>(allocations))
                                .build())
                        .build())
                .build();
        final String requestId = "1";

        assertThat(provider.constructParams(payload, requestId))
                .isEqualTo(Map.of(
                        "activityLevels", activityLevels,
                        "allocations", allocations,
                        "allocationsPerYear", totalAllocations
                ));
        assertThat(activityLevels).containsExactly(first, second, third, fourth);
    }
}
