package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2AmendsSubmitService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRS2ApplicationAmendsSaveActionHandlerTest {

    @InjectMocks
    private BDRS2ApplicationAmendsSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private BDRS2AmendsSubmitService submitService;

    @Test
    public void process() {
        final Long requestTaskId = 1L;
        final AppUser user = AppUser.builder().userId("user").build();

        BDRS2ApplicationAmendsSaveRequestTaskActionPayload taskActionPayload = BDRS2ApplicationAmendsSaveRequestTaskActionPayload
                .builder().build();

        RequestTask requestTask = RequestTask
                .builder()
                .id(requestTaskId)
                .type(RequestTaskType.BDRS2_APPLICATION_AMENDS_SUBMIT)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, RequestTaskActionType.BDRS2_APPLICATION_AMENDS_SAVE, user, taskActionPayload);

        verify(submitService, times(1)).saveAmends(taskActionPayload, requestTask);
    }

    @Test
    public void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.BDRS2_APPLICATION_AMENDS_SAVE);
    }
}
