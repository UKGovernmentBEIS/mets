package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETICompletedDocumentTemplateWorkflowParamsProvider;

import java.util.List;
import java.util.Map;

@Service
public class HSETIOfficialNoticePreviewHandler extends PreviewDocumentAbstractHandler  {

    private final String previewFileName = "letter_preview.pdf";
    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final HSETICompletedDocumentTemplateWorkflowParamsProvider paramsProvider;

    public HSETIOfficialNoticePreviewHandler(RequestTaskService requestTaskService,
                                            final InstallationPreviewOfficialNoticeService previewOfficialNoticeService,
                                            final DocumentFileGeneratorService documentFileGeneratorService,
                                            final HSETICompletedDocumentTemplateWorkflowParamsProvider paramsProvider) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
        this.paramsProvider = paramsProvider;
    }
    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (HSETIApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();

        final TemplateParams templateParams =
                previewOfficialNoticeService.generateCommonParams(request, decisionNotification);


        final Map<String, Object> params = paramsProvider
                .constructParams(taskPayload);

        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.HSE_TI_COMPLETED,
                templateParams,
                previewFileName);

    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.HSE_TI_COMPLETED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(
            RequestTaskType.HSE_TI_APPLICATION_REGULATOR_REVIEW_SUBMIT,
            RequestTaskType.HSE_TI_APPLICATION_PEER_REVIEW
        );
    }
}
