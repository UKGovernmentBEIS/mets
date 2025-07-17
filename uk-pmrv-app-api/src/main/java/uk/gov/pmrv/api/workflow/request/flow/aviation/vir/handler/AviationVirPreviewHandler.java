package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;

import java.util.List;

@Service
public class AviationVirPreviewHandler extends PreviewDocumentAbstractHandler {

    private final AviationVirOfficialNoticeService aviationVirOfficialNoticeService;
    private final RequestService requestService;


    public AviationVirPreviewHandler(final RequestTaskService requestTaskService,
                                     final AviationVirOfficialNoticeService aviationVirOfficialNoticeService,
                                     final RequestService requestService

    ) {
        super(requestTaskService);
        this.aviationVirOfficialNoticeService = aviationVirOfficialNoticeService;
        this.requestService = requestService;
    }

    @Override
    protected FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        Request dbRequest = requestService.findRequestById(requestTask.getRequest().getId());
        AviationVirApplicationReviewRequestTaskPayload payload =(AviationVirApplicationReviewRequestTaskPayload) requestTask.getPayload();
        ((AviationVirRequestPayload) dbRequest.getPayload()).setRegulatorReviewResponse(payload.getRegulatorReviewResponse());
        ((AviationVirRequestPayload) dbRequest.getPayload()).setDecisionNotification(decisionNotification);
        return aviationVirOfficialNoticeService.doGenerateOfficialNoticeWithoutSave(dbRequest);
    }


    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.AVIATION_VIR_REVIEWED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.AVIATION_VIR_APPLICATION_REVIEW);
    }
}
