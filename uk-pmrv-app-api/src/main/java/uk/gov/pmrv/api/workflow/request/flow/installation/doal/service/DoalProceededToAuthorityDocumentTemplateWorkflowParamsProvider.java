package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.utils.DoalTotalAllocationsUtils;

import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

@Component
public class DoalProceededToAuthorityDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<DoalRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.DOAL_SUBMITTED;
    }

    @Override
    public Map<String, Object> constructParams(DoalRequestPayload payload, String requestId) {
        List<ActivityLevel> activityLevels = payload.getDoal().getActivityLevelChangeInformation().getActivityLevels();
        activityLevels.sort(Comparator.comparing(ActivityLevel::getYear, Year::compareTo)
                .thenComparing(ActivityLevel::getSubInstallationName));

        SortedSet<PreliminaryAllocation> preliminaryAllocations = payload.getDoal().getActivityLevelChangeInformation()
                .getPreliminaryAllocations();
        Map<Year, Integer> totalAllocationsPerYear = DoalTotalAllocationsUtils.generateTotalAllocationsPerYear(preliminaryAllocations);

        return Map.of(
                "activityLevels", activityLevels,
                "allocations", preliminaryAllocations,
                "allocationsPerYear", totalAllocationsPerYear
        );
    }
}
