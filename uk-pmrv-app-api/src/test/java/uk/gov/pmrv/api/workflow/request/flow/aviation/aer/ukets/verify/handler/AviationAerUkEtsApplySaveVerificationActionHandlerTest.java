package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.service.RequestAviationAerUkEtsApplyVerificationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsApplySaveVerificationActionHandlerTest {

    @InjectMocks
    private AviationAerUkEtsApplySaveVerificationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationAerUkEtsApplyVerificationService aerUkEtsApplyVerificationService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        AppUser user = AppUser.builder().build();
        AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload taskActionPayload =
            AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload.builder()
            .payloadType(RequestTaskActionPayloadType.AVIATION_AER_UKETS_SAVE_APPLICATION_VERIFICATION_PAYLOAD)
            .build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, RequestTaskActionType.AVIATION_AER_UKETS_SAVE_APPLICATION_VERIFICATION, user, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1))
            .findTaskById(requestTaskId);
        verify(aerUkEtsApplyVerificationService, times(1))
            .applySaveAction(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_UKETS_SAVE_APPLICATION_VERIFICATION);
    }
}