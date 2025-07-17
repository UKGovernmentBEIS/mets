package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;


import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.enumeration.AviationAerCorsia3YearPeriodOffsettingSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsia3YearPeriodOffsettingSubmitNotifyOperatorActionHandlerTest {

    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingSubmitNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationAerCorsia3YearPeriodOffsettingSubmitService submitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_NOTIFY_OPERATOR;
        AppUser appUser = AppUser.builder().build();
        DecisionNotification decisionNotification = DecisionNotification.builder().signatory("sign").build();

        NotifyOperatorForDecisionRequestTaskActionPayload payload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_NOTIFY_OPERATOR_PAYLOAD)
                .decisionNotification(decisionNotification)
                .build();

        Request request = Request.builder().type(RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING).id("2").build();

        final Map<String, Boolean> aviationAerCorsia3YearPeriodOffsettingSectionsCompleted = new HashMap<>();
        aviationAerCorsia3YearPeriodOffsettingSectionsCompleted.put("aviationAerCorsia3YearPeriodOffsetting",false);
        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting = AviationAerCorsia3YearPeriodOffsetting
                .builder()
                .build();

        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload taskPayload = AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD)
                .aviationAerCorsia3YearPeriodOffsettingSectionsCompleted(aviationAerCorsia3YearPeriodOffsettingSectionsCompleted)
                .aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting)
                .decisionNotification(decisionNotification)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId("processTaskId")
                .payload(taskPayload)
                .request(request)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType,  appUser, payload);

        assertThat(request.getSubmissionDate()).isNotNull();

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(submitService, times(1)).applySubmitNotify(requestTask, payload.getDecisionNotification(), appUser);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_OUTCOME, AviationAerCorsia3YearPeriodOffsettingSubmitOutcome.SUBMITTED));
    }
}
