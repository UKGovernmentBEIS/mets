package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

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
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaFee;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaFeeDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service.AviationDoECorsiaSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaSubmitNotifyOperatorActionHandlerTest {

    @InjectMocks
    private AviationDoECorsiaSubmitNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationDoECorsiaSubmitService submitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process_with_fee_set_payment_process_vars() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR;
        AppUser appUser = AppUser.builder().userId("user").build();
        NotifyOperatorForDecisionRequestTaskActionPayload payload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
            .payloadType(RequestTaskActionPayloadType.AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR_PAYLOAD)
            .decisionNotification(DecisionNotification.builder()
                .signatory("signatory")
                .build())
            .build();
        Request request = Request.builder().id("2").build();

        UUID att1 = UUID.randomUUID();
        LocalDate dueDate = LocalDate.now().plusDays(1);
        AviationDoECorsia doe = AviationDoECorsia.builder()
            .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                .furtherDetails("furtherDetails")
                .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                .build())
            .fee(AviationDoECorsiaFee.builder()
                .chargeOperator(true)
                .feeDetails(AviationDoECorsiaFeeDetails.builder()
                    .hourlyRate(BigDecimal.TEN)
                    .totalBillableHours(BigDecimal.TEN)
                    .dueDate(dueDate)
                    .build())
                .build())
            .build();
        AviationDoECorsiaApplicationSubmitRequestTaskPayload taskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
            .doe(doe)
            .doeAttachments(Map.of(att1, "atta1.pdf"))
            .sectionCompleted(true)
            .build();

        RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId("processTaskId")
            .payload(taskPayload)
            .request(request)
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType,  appUser, payload);

        assertThat(request.getSubmissionDate()).isNotNull();

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(submitService, times(1)).applySubmitNotify(requestTask, payload.getDecisionNotification(), appUser);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.AVIATION_DOE_CORSIA_SUBMIT_OUTCOME, AviationDoECorsiaSubmitOutcome.SUBMITTED,
                BpmnProcessConstants.AVIATION_DOE_CORSIA_IS_PAYMENT_REQUIRED, true,
                BpmnProcessConstants.PAYMENT_EXPIRATION_DATE, DateUtils.atEndOfDay(doe.getFee().getFeeDetails().getDueDate())));
    }

    @Test
    void process_with_fee_do_not_set_payment_process_vars() {
         Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR;
        AppUser appUser = AppUser.builder().userId("user").build();
        NotifyOperatorForDecisionRequestTaskActionPayload payload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
            .payloadType(RequestTaskActionPayloadType.AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR_PAYLOAD)
            .decisionNotification(DecisionNotification.builder()
                .signatory("signatory")
                .build())
            .build();
        Request request = Request.builder().id("2").build();

        UUID att1 = UUID.randomUUID();
        LocalDate dueDate = LocalDate.now().plusDays(1);
        AviationDoECorsia doe = AviationDoECorsia.builder()
            .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                .furtherDetails("furtherDetails")
                .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                .build())
            .fee(AviationDoECorsiaFee.builder()
                .chargeOperator(false)
                .feeDetails(AviationDoECorsiaFeeDetails.builder()
                    .hourlyRate(BigDecimal.TEN)
                    .totalBillableHours(BigDecimal.TEN)
                    .dueDate(dueDate)
                    .build())
                .build())
            .build();
        AviationDoECorsiaApplicationSubmitRequestTaskPayload taskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
            .doe(doe)
            .doeAttachments(Map.of(att1, "atta1.pdf"))
            .sectionCompleted(true)
            .build();

        RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId("processTaskId")
            .payload(taskPayload)
            .request(request)
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType,  appUser, payload);

        assertThat(request.getSubmissionDate()).isNotNull();

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(submitService, times(1)).applySubmitNotify(requestTask, payload.getDecisionNotification(), appUser);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.AVIATION_DOE_CORSIA_SUBMIT_OUTCOME, AviationDoECorsiaSubmitOutcome.SUBMITTED,
                BpmnProcessConstants.AVIATION_DOE_CORSIA_IS_PAYMENT_REQUIRED, false));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR);
    }
}
