package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.AviationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service.AviationDoECorsiaSubmittedDocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType.AVIATION_DOE_SUBMITTED;

@Service
public class AviationDoECorsiaSubmitOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final AviationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final AviationDoECorsiaSubmittedDocumentTemplateWorkflowParamsProvider templateWorkflowParamsProvider;

    public AviationDoECorsiaSubmitOfficialLetterPreviewHandler(RequestTaskService requestTaskService, AviationPreviewOfficialNoticeService previewOfficialNoticeService, DocumentFileGeneratorService documentFileGeneratorService, AviationDoECorsiaSubmittedDocumentTemplateWorkflowParamsProvider templateWorkflowParamsProvider) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
        this.templateWorkflowParamsProvider = templateWorkflowParamsProvider;
    }

    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final AviationDoECorsiaApplicationSubmitRequestTaskPayload taskPayload =
                (AviationDoECorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final AviationDoECorsiaRequestPayload requestPayload = (AviationDoECorsiaRequestPayload) request.getPayload();

        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification, false);
        final Map<String, Object> params = templateWorkflowParamsProvider.constructParams(taskPayload, requestPayload.getReportingYear(), request.getAccountId());
        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                AVIATION_DOE_SUBMITTED,
                templateParams,
                "DoE_notice.pdf"
        );
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT,RequestTaskType.AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW );
    }


    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(AVIATION_DOE_SUBMITTED);
    }
}
