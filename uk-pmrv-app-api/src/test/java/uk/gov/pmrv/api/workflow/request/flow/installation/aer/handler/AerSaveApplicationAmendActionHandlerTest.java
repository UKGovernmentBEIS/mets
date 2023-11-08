package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerReviewService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerSaveApplicationAmendActionHandlerTest {
    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AerReviewService aerReviewService;

    @InjectMocks
    private AerSaveApplicationAmendActionHandler actionHandler;

    @Test
    void process_should_save_the_aer_amends() {
        Long requestTaskId = 1L;
        RequestTaskActionType actionType = RequestTaskActionType.AER_SAVE_APPLICATION_AMEND;
        PmrvUser pmrvUser = new PmrvUser();
        AerSaveApplicationAmendRequestTaskActionPayload payload = new AerSaveApplicationAmendRequestTaskActionPayload();

        RequestTask requestTask = new RequestTask();
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        actionHandler.process(requestTaskId, actionType, pmrvUser, payload);

        verify(requestTaskService).findTaskById(requestTaskId);
        verify(aerReviewService).saveAmendOfAer(payload, requestTask);
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> expectedTypes = List.of(RequestTaskActionType.AER_SAVE_APPLICATION_AMEND);
        assertEquals(expectedTypes, actionHandler.getTypes());
    }
}
