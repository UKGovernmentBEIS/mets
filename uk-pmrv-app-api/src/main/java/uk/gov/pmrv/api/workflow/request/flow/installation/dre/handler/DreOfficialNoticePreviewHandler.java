package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreOfficialNoticeGenerateService;

import java.util.List;

@Service
public class DreOfficialNoticePreviewHandler extends PreviewDocumentAbstractHandler {

    private final DreOfficialNoticeGenerateService dreOfficialNoticeGenerateService;
    private final RequestService requestService;


    public DreOfficialNoticePreviewHandler(final RequestTaskService requestTaskService,
                                           final DreOfficialNoticeGenerateService dreOfficialNoticeGenerateService,
                                           final RequestService requestService

    ) {
        super(requestTaskService);
        this.dreOfficialNoticeGenerateService = dreOfficialNoticeGenerateService;
        this.requestService = requestService;
    }

    @Override
    protected FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        Request dbRequest = requestService.findRequestById(requestTask.getRequest().getId());

        DreRequestPayload payload = (DreRequestPayload) dbRequest.getPayload();
        payload.setDre(((DreApplicationSubmitRequestTaskPayload) requestTask.getPayload()).getDre());
        dbRequest.setPayload(payload);

        return dreOfficialNoticeGenerateService.doGenerateOfficialNoticeWithoutSave(dbRequest, decisionNotification);
    }


    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.DRE_SUBMITTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.DRE_APPLICATION_SUBMIT,RequestTaskType.DRE_APPLICATION_PEER_REVIEW);
    }
}
