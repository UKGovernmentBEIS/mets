package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.utils.DoalTotalAllocationsUtils;

import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

@Service
public class DoalProceededToAuthorityOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;

    public DoalProceededToAuthorityOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                                final InstallationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                                final DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
    }

    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final DoalApplicationSubmitRequestTaskPayload taskPayload =
                (DoalApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification);

        final Map<String, Object> params = this.constructParams(taskPayload);
        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.DOAL_SUBMITTED,
                templateParams,
                "Activity_level_determination_preliminary_allocation_letter.pdf");
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.DOAL_SUBMITTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.DOAL_APPLICATION_SUBMIT);
    }

    private Map<String, Object> constructParams(DoalApplicationSubmitRequestTaskPayload taskPayload) {
        List<ActivityLevel> activityLevels = taskPayload.getDoal().getActivityLevelChangeInformation().getActivityLevels();
        activityLevels.sort(Comparator.comparing(ActivityLevel::getYear, Year::compareTo)
                .thenComparing(ActivityLevel::getSubInstallationName));
        SortedSet<PreliminaryAllocation> preliminaryAllocations = taskPayload.getDoal().getActivityLevelChangeInformation()
                .getPreliminaryAllocations();
        Map<Year, Integer> totalAllocationsPerYear = DoalTotalAllocationsUtils.generateTotalAllocationsPerYear(preliminaryAllocations);
        return Map.of(
                "activityLevels", activityLevels,
                "allocations", preliminaryAllocations,
                "allocationsPerYear", totalAllocationsPerYear
        );
    }
}
