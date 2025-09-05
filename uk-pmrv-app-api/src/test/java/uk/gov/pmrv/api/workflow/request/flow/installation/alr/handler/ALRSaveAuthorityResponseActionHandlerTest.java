package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRSaveAuthorityResponseTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRAuthorityResponseService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ALRSaveAuthorityResponseActionHandlerTest {

    @InjectMocks
    private ALRSaveAuthorityResponseActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private ALRAuthorityResponseService alrAuthorityResponseService;

    @Test
    void doProcess() {
        final ALRSaveAuthorityResponseTaskActionPayload taskActionPayload =
                ALRSaveAuthorityResponseTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.ALR_SAVE_AUTHORITY_RESPONSE_PAYLOAD)
                        .build();
        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(), RequestTaskActionType.ALR_SAVE_APPLICATION, appUser,
                taskActionPayload);

        // Verify
        verify(alrAuthorityResponseService, times(1))
                .applyAuthorityResponseSaveAction(requestTask, taskActionPayload);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.ALR_SAVE_AUTHORITY_RESPONSE);
    }
}
