package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import java.time.Year;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.enumeration.AviationAerCorsiaAnnualOffsettingSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsiaAnnualOffsettingSubmitNotifyOperatorActionHandlerTest {

    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingSubmitNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationAerCorsiaAnnualOffsettingSubmitService aviationAerCorsiaAnnualOffsettingSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_NOTIFY_OPERATOR;
        AppUser appUser = AppUser.builder().build();
        DecisionNotification decisionNotification = DecisionNotification.builder().signatory("sign").build();

        NotifyOperatorForDecisionRequestTaskActionPayload payload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_NOTIFY_OPERATOR_PAYLOAD)
                .decisionNotification(decisionNotification)
                .build();

        Request request = Request.builder().type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING).id("2").build();

        final Map<String, Boolean> aviationAerCorsiaAnnualOffsettingSectionsCompleted = new HashMap<>();
        aviationAerCorsiaAnnualOffsettingSectionsCompleted.put("aviationAerCorsiaAnnualOffsetting",false);
        AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting = AviationAerCorsiaAnnualOffsetting
                .builder()
                .calculatedAnnualOffsetting(6)
                .schemeYear(Year.of(2023))
                .sectorGrowth(2.9)
                .totalChapter(1)
                .build();

        AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload taskPayload = AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .aviationAerCorsiaAnnualOffsettingSectionsCompleted(aviationAerCorsiaAnnualOffsettingSectionsCompleted)
                .aviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting)
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
        verify(aviationAerCorsiaAnnualOffsettingSubmitService, times(1)).applySubmitNotify(requestTask, payload.getDecisionNotification(), appUser);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_OUTCOME, AviationAerCorsiaAnnualOffsettingSubmitOutcome.SUBMITTED));
    }


}
