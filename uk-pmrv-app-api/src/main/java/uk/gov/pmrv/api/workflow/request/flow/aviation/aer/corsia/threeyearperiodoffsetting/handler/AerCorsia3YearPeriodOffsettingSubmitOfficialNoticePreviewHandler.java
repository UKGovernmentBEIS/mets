package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;


@Service
public class AerCorsia3YearPeriodOffsettingSubmitOfficialNoticePreviewHandler extends PreviewDocumentAbstractHandler {


    private final AviationAerCorsia3YearPeriodOffsettingOfficialNoticeService officialNoticeService;

    public AerCorsia3YearPeriodOffsettingSubmitOfficialNoticePreviewHandler(RequestTaskService
                                                                       requestTaskService,
                                                                 final AviationAerCorsia3YearPeriodOffsettingOfficialNoticeService
                                                                            officialNoticeService) {
        super(requestTaskService);
        this.officialNoticeService = officialNoticeService;
    }

    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload taskPayload =
                (AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();

        AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload =
                (AviationAerCorsia3YearPeriodOffsettingRequestPayload) request.getPayload();

        requestPayload
                .setAviationAerCorsia3YearPeriodOffsetting(
                        ((AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload)
                                requestTask.getPayload()).getAviationAerCorsia3YearPeriodOffsetting());

        request.setPayload(requestPayload);

        return officialNoticeService
                .doGenerateOfficialNoticeWithoutSave(request, decisionNotification);
    }


    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.AVIATION_AER_CORSIA_3_YEAR_PERIOD_OFFSETTING_SUBMITTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(
            RequestTaskType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT,
            RequestTaskType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW
        );
    }

}
