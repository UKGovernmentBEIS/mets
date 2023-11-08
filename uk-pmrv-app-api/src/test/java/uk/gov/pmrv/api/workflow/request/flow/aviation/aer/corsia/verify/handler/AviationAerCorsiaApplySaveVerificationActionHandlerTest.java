package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.service.RequestAviationAerCorsiaApplyVerificationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaApplySaveVerificationActionHandlerTest {

    @InjectMocks
    private AviationAerCorsiaApplySaveVerificationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationAerCorsiaApplyVerificationService requestAviationAerCorsiaApplyVerificationService;

    @Test
    void process() {
        final Long requestTaskId = 1L;
        final PmrvUser user = PmrvUser.builder().build();
        final AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload taskActionPayload =
                AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD)
                        .build();
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION, user, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1))
                .findTaskById(requestTaskId);
        verify(requestAviationAerCorsiaApplyVerificationService, times(1))
                .applySaveAction(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION);
    }
}
