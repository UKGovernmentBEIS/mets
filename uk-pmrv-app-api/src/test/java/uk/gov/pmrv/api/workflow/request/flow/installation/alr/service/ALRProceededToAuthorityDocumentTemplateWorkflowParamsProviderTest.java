package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRActivityLevel;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRPreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ALRProceededToAuthorityDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private ALRProceededToAuthorityDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.ALR_SUBMITTED);
    }

    @Test
    void constructParams() {
        final Set<ALRPreliminaryAllocation> allocations = Set.of(
                ALRPreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(Year.of(2020))
                        .allowances(10)
                        .build(),
                ALRPreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AMMONIA)
                        .year(Year.of(2020))
                        .allowances(10)
                        .build(),
                ALRPreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AMMONIA)
                        .year(Year.of(2021))
                        .allowances(100)
                        .build()
        );
        List<ALRActivityLevel> activityLevels = new ArrayList<>();
        ALRActivityLevel fourth = ALRActivityLevel.builder()
                .year(Year.of(2020))
                .subInstallationName(SubInstallationName.DOLIME)
                .changeType(ChangeType.DECREASE)
                .changedActivityLevel("-1%")
                .comments("Comments")
                .build();
        activityLevels.add(fourth);
        ALRActivityLevel third = ALRActivityLevel.builder()
                .year(Year.of(2020))
                .subInstallationName(SubInstallationName.DOLIME)
                .changeType(ChangeType.DECREASE)
                .changedActivityLevel("-1%")
                .comments("Comments")
                .build();
        activityLevels.add(third);

        ALRActivityLevel second = ALRActivityLevel.builder()
                .year(Year.of(2020))
                .subInstallationName(SubInstallationName.ALUMINIUM)
                .changeType(ChangeType.DECREASE)
                .changedActivityLevel("-1%")
                .comments("Comments")
                .build();
        activityLevels.add(second);

        ALRActivityLevel first = ALRActivityLevel.builder()
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

        final ALRRequestPayload payload = ALRRequestPayload.builder()
                .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().allocations(allocations).activityLevels(activityLevels)
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
