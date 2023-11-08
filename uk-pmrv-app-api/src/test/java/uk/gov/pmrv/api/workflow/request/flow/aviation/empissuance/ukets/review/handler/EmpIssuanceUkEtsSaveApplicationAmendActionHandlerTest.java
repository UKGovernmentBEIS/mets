package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.RequestEmpUkEtsReviewService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsSaveApplicationAmendActionHandlerTest {

    @InjectMocks
    private EmpIssuanceUkEtsSaveApplicationAmendActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestEmpUkEtsReviewService requestEmpUkEtsReviewService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().build();
        EmpIssuanceUkEtsSaveApplicationAmendRequestTaskActionPayload actionPayload =
                EmpIssuanceUkEtsSaveApplicationAmendRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_AMEND_PAYLOAD)
                        .build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_AMEND, pmrvUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestEmpUkEtsReviewService, times(1)).saveAmend(actionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsOnly(RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_AMEND);
    }
}
