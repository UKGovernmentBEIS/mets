package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.TransferParty;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferAApplyService;

@ExtendWith(MockitoExtension.class)
class PermitTransferASubmitApplicationActionHandlerTest {

    @InjectMocks
    private PermitTransferASubmitApplicationActionHandler handler;

    @Mock
    private PermitTransferAApplyService applyService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private DateService dateService;

    @Test
    void process() {

        final RequestTaskActionEmptyPayload submitPayload =
            RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask =
            RequestTask.builder()
                .id(1L)
                .request(request)
                .payload(PermitTransferAApplicationRequestTaskPayload.builder()
                    .permitTransferDetails(PermitTransferDetails.builder().payer(TransferParty.TRANSFERER).build()).build())
                .processTaskId(processTaskId)
                .build();
        final LocalDateTime submissionDate = LocalDateTime.of(2023, 1, 2, 3, 4);

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        when(dateService.getLocalDateTime()).thenReturn(submissionDate);

        //invoke
        handler.process(
            requestTask.getId(),
            RequestTaskActionType.PERMIT_TRANSFER_A_SUBMIT_APPLICATION,
            pmrvUser,
            submitPayload
        );

        assertThat(request.getSubmissionDate()).isEqualTo(submissionDate);
        
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(dateService, times(1)).getLocalDateTime();
        verify(applyService, times(1)).applySubmitAction(requestTask, pmrvUser);
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(
                BpmnProcessConstants.PERMIT_TRANSFER_SUBMIT_OUTCOME, PermitTransferOutcome.SUBMITTED
            ));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_TRANSFER_A_SUBMIT_APPLICATION);
    }
}