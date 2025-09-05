package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRActivityLevel;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRPreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.utils.DoalTotalAllocationsUtils;

import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ALRProceededToAuthorityDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<ALRRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.ALR_SUBMITTED;
    }

    @Override
    public Map<String, Object> constructParams(ALRRequestPayload payload, String requestId) {
        List<ALRActivityLevel> activityLevels = payload.getRegulatorReviewOutcome().getActivityLevels();
        activityLevels.sort(Comparator.comparing(ActivityLevel::getYear, Year::compareTo)
                .thenComparing(ActivityLevel::getSubInstallationName));

        Set<ALRPreliminaryAllocation> alrPreliminaryAllocations = payload.getRegulatorReviewOutcome().getAllocations();
        Set<PreliminaryAllocation> preliminaryAllocations = alrPreliminaryAllocations.stream()
                .map(pa -> (PreliminaryAllocation) pa)
                .collect(Collectors.toSet());
        Map<Year, Integer> totalAllocationsPerYear = DoalTotalAllocationsUtils.generateTotalAllocationsPerYear(preliminaryAllocations);

        return Map.of(
                "activityLevels", activityLevels,
                "allocations", preliminaryAllocations,
                "allocationsPerYear", totalAllocationsPerYear
        );
    }
}
