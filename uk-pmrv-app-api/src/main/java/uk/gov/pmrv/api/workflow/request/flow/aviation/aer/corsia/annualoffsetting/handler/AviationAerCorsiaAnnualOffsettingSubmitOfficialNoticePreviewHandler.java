package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;

@Service
public class AviationAerCorsiaAnnualOffsettingSubmitOfficialNoticePreviewHandler
        extends PreviewDocumentAbstractHandler {

    private final AviationAerCorsiaAnnualOffsettingOfficialNoticeService aviationAerCorsiaAnnualOffsettingOfficialNoticeService;


    public AviationAerCorsiaAnnualOffsettingSubmitOfficialNoticePreviewHandler(RequestTaskService requestTaskService,
                                                                               final AviationAerCorsiaAnnualOffsettingOfficialNoticeService
                                                                               aviationAerCorsiaAnnualOffsettingOfficialNoticeService) {
        super(requestTaskService);
        this.aviationAerCorsiaAnnualOffsettingOfficialNoticeService =
                aviationAerCorsiaAnnualOffsettingOfficialNoticeService;
    }


    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload taskPayload =
                (AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();

        AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload =
                (AviationAerCorsiaAnnualOffsettingRequestPayload) request.getPayload();

        requestPayload
                .setAviationAerCorsiaAnnualOffsetting(
                        ((AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload)
                                requestTask.getPayload()).getAviationAerCorsiaAnnualOffsetting());

        request.setPayload(requestPayload);

        return aviationAerCorsiaAnnualOffsettingOfficialNoticeService
                .doGenerateOfficialNoticeWithoutSave(request, decisionNotification);

    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMITTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(
            RequestTaskType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT,
                RequestTaskType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW
        );
    }
}
