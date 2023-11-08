package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBApplyService;

@ExtendWith(MockitoExtension.class)
class PermitTransferBSaveApplicationActionHandlerTest {

    @InjectMocks
    private PermitTransferBSaveApplicationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitTransferBApplyService applyService;

    @Test
    void doProcess() {

        final PermitTransferBSaveApplicationRequestTaskActionPayload taskActionPayload =
            PermitTransferBSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_TRANSFER_B_SAVE_APPLICATION_PAYLOAD)
                .build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask =
            RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_TRANSFER_B_SAVE_APPLICATION,
            pmrvUser,
            taskActionPayload);

        verify(applyService, times(1)).applySaveAction(requestTask, taskActionPayload);
    }
}
