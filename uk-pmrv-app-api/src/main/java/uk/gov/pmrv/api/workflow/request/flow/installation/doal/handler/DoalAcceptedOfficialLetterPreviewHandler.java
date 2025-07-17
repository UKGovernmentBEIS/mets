package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.springframework.stereotype.Service;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

import java.util.List;
import java.util.Map;

@Service
public class DoalAcceptedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;

    public DoalAcceptedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                    final InstallationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                    final DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
    }

    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final DoalAuthorityResponseRequestTaskPayload taskPayload =
                (DoalAuthorityResponseRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();
        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification);

        final Map<String, Object> params = this.constructParams(taskPayload, requestPayload);
        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.DOAL_ACCEPTED,
                templateParams,
                "Activity_level_determination_approved_by_Authority_notice.pdf");
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.DOAL_ACCEPTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.DOAL_AUTHORITY_RESPONSE);
    }

    private Map<String, Object> constructParams(DoalAuthorityResponseRequestTaskPayload taskPayload, DoalRequestPayload requestPayload) {
        return Map.of(
                "reportingYear", requestPayload.getReportingYear(),
                "doal", requestPayload.getDoal(),
                "authorityResponse", taskPayload.getDoalAuthority().getAuthorityResponse()
        );
    }
}
