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
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERVerificationSubmitService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERApplicationVerificationSaveActionHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NERVerificationSubmitService nerVerificationSubmitService;

    @InjectMocks
    private NERApplicationVerificationSaveActionHandler handler;

    @Test
    void process_shouldDelegateToService() {
        // Arrange
        Long requestTaskId = 1L;
        RequestTaskActionType actionType = RequestTaskActionType.NER_APPLICATION_SAVE_VERIFICATION;
        AppUser appUser = new AppUser();

        NERApplicationVerificationSaveRequestTaskActionPayload payload =
                NERApplicationVerificationSaveRequestTaskActionPayload.builder().build();

        RequestTask requestTask = new RequestTask();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Act
        handler.process(requestTaskId, actionType, appUser, payload);

        // Assert
        verify(requestTaskService).findTaskById(requestTaskId);
        verify(nerVerificationSubmitService)
                .applySaveAction(payload, requestTask);
    }

    @Test
    void getTypes_shouldReturnCorrectActionType() {
        // Act
        List<RequestTaskActionType> types = handler.getTypes();

        // Assert
        assertEquals(1, types.size());
        assertEquals(
                RequestTaskActionType.NER_APPLICATION_SAVE_VERIFICATION,
                types.getFirst());
    }
}
