package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.service.RequestAviationAerCorsiaReviewService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaSaveApplicationAmendActionHandlerTest {

    @InjectMocks
    private AviationAerCorsiaSaveApplicationAmendActionHandler saveApplicationAmendActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationAerCorsiaReviewService aviationAerCorsiaReviewService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        AviationAerCorsiaSaveApplicationAmendRequestTaskActionPayload taskActionPayload =
                AviationAerCorsiaSaveApplicationAmendRequestTaskActionPayload.builder().build();
        AppUser user = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        saveApplicationAmendActionHandler
                .process(requestTaskId, RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_APPLICATION_AMEND, user, taskActionPayload);

        //verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(aviationAerCorsiaReviewService, times(1)).saveAerAmend(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(saveApplicationAmendActionHandler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_APPLICATION_AMEND);
    }
}
