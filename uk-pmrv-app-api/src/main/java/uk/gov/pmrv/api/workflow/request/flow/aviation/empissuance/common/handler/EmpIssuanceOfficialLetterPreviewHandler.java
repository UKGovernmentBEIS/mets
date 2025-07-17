package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.AviationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;


@Service
public abstract class EmpIssuanceOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final AviationPreviewOfficialNoticeService previewOfficialNoticeService;

    public EmpIssuanceOfficialLetterPreviewHandler(RequestTaskService requestTaskService,
                                                   AviationPreviewOfficialNoticeService
                                                                 previewOfficialNoticeService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
    }

    protected TemplateParams constructTemplateParams(final long taskId,
                                                     final DecisionNotification decisionNotification) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final Request request = requestTask.getRequest();
        final TemplateParams templateParams = previewOfficialNoticeService
                .generateCommonParamsWithoutAccountNameLocation(request, decisionNotification);
        final String operatorName = this.getOperatorNameFromRequestTaskPayload(requestTask.getPayload());
        templateParams.getAccountParams().setName(operatorName);

        return templateParams;
    }


    protected abstract String getOperatorNameFromRequestTaskPayload(RequestTaskPayload requestTaskPayload);
}
