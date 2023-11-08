package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalSaveAuthorityResponseTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalAuthorityResponseService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalSaveAuthorityResponseActionHandlerTest {

    @InjectMocks
    private DoalSaveAuthorityResponseActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private DoalAuthorityResponseService doalAuthorityResponseService;

    @Test
    void doProcess() {
        final DoalSaveAuthorityResponseTaskActionPayload taskActionPayload =
                DoalSaveAuthorityResponseTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.DOAL_SAVE_AUTHORITY_RESPONSE_PAYLOAD)
                        .build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(), RequestTaskActionType.DOAL_SAVE_APPLICATION, pmrvUser,
                taskActionPayload);

        // Verify
        verify(doalAuthorityResponseService, times(1))
                .applyAuthorityResponseSaveAction(requestTask, taskActionPayload);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.DOAL_SAVE_AUTHORITY_RESPONSE);
    }
}
