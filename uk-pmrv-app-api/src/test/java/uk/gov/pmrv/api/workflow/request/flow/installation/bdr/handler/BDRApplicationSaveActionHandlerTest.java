package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;


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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRSubmitService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationSaveActionHandlerTest {

    @InjectMocks
    private BDRApplicationSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private BDRSubmitService submitService;


    @Test
    void process() {
        final BDRApplicationSaveRequestTaskActionPayload taskActionPayload =
                BDRApplicationSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.BDR_APPLICATION_SAVE_PAYLOAD)
                        .build();

        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(), RequestTaskActionType.BDR_SAVE_APPLICATION, appUser,
                taskActionPayload);

        verify(submitService, times(1)).applySaveAction(requestTask, taskActionPayload);
    }


    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.BDR_SAVE_APPLICATION);
    }
}
