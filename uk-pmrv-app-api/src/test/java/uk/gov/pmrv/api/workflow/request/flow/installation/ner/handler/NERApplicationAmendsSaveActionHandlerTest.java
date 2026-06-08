package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERAmendsSubmitService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERApplicationAmendsSaveActionHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NERAmendsSubmitService submitService;

    @InjectMocks
    private NERApplicationAmendsSaveActionHandler handler;

    @Test
    void process() {
        // given
        Long requestTaskId = 1L;

        NERApplicationAmendsSaveRequestTaskActionPayload payload =
                NERApplicationAmendsSaveRequestTaskActionPayload.builder().build();

        RequestTask requestTask = RequestTask.builder().build();

        when(requestTaskService.findTaskById(requestTaskId))
                .thenReturn(requestTask);

        // when
        handler.process(
                requestTaskId,
                RequestTaskActionType.NER_APPLICATION_AMENDS_SAVE,
                AppUser.builder().build(),
                payload);

        // then
        verify(requestTaskService).findTaskById(requestTaskId);

        verify(submitService).saveAmends(payload, requestTask);
    }

    @Test
    void getTypes() {
        assertEquals(
                List.of(RequestTaskActionType.NER_APPLICATION_AMENDS_SAVE),
                handler.getTypes());
    }
}
