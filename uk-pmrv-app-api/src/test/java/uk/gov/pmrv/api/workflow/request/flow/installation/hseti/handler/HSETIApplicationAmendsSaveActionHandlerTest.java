package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;


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
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIAmendsSubmitService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HSETIApplicationAmendsSaveActionHandlerTest {

    @InjectMocks
    private HSETIApplicationAmendsSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private HSETIAmendsSubmitService submitService;

    @Test
    public void process() {

        final Long requestTaskId = 1L;
        final AppUser user = AppUser.builder().userId("user").build();

        HSETIApplicationAmendsSaveRequestTaskActionPayload taskActionPayload = HSETIApplicationAmendsSaveRequestTaskActionPayload
                .builder().build();

        RequestTask requestTask = RequestTask
              .builder()
              .id(requestTaskId)
              .type(RequestTaskType.HSE_TI_APPLICATION_AMENDS_SUBMIT)
              .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, RequestTaskActionType.HSE_TI_APPLICATION_AMENDS_SAVE, user, taskActionPayload);

        verify(submitService, times(1)).saveAmends(taskActionPayload,requestTask);
    }


    @Test
    public void  getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.HSE_TI_APPLICATION_AMENDS_SAVE);
    }
}
