package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.enumeration.AviationAerCorsia3YearPeriodOffsettingSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;



@Component
@RequiredArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingSubmitNotifyOperatorActionHandler
        implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AviationAerCorsia3YearPeriodOffsettingSubmitService submitService;
    private final WorkflowService workflowService;


    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();

        request.setSubmissionDate(LocalDateTime.now());

        submitService
                .applySubmitNotify(requestTask, payload.getDecisionNotification(), appUser);

        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID,
                requestTask.getRequest().getId(),
                BpmnProcessConstants.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_OUTCOME,
                AviationAerCorsia3YearPeriodOffsettingSubmitOutcome.SUBMITTED
        ));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_NOTIFY_OPERATOR);
    }
}
