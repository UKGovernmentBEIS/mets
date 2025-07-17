package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmitSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service.AviationDoECorsiaSubmitService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaSubmitSaveActionHandlerTest {

    @InjectMocks
    private AviationDoECorsiaSubmitSaveActionHandler handler;

    @Mock
    private AviationDoECorsiaSubmitService submitService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        AppUser appUser = AppUser.builder().build();
        AviationDoECorsiaSubmitSaveRequestTaskActionPayload taskActionPayload =
            AviationDoECorsiaSubmitSaveRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_DOE_CORSIA_SUBMIT_SAVE_PAYLOAD)
                .doe(AviationDoECorsia.builder().build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTask.getId(), RequestTaskActionType.AVIATION_DOE_CORSIA_SUBMIT_SAVE,
            appUser, taskActionPayload);

        // Verify
        verify(submitService, times(1)).applySaveAction(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
            .containsOnly(RequestTaskActionType.AVIATION_DOE_CORSIA_SUBMIT_SAVE);
    }
}
