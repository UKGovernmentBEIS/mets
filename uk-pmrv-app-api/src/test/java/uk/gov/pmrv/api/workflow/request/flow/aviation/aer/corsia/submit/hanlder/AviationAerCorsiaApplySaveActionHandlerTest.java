package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.hanlder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.handler.AviationAerCorsiaApplySaveActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.service.RequestAviationAerCorsiaApplyService;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaApplySaveActionHandlerTest {

    @InjectMocks
    private AviationAerCorsiaApplySaveActionHandler applySaveActionHandler;

    @Mock
    private RequestAviationAerCorsiaApplyService applyService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        
        Long requestTaskId = 1L;
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        AppUser appUser = AppUser.builder().build();
        AviationAerCorsiaSaveApplicationRequestTaskActionPayload taskActionPayload =
            AviationAerCorsiaSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_AER_CORSIA_SAVE_APPLICATION_PAYLOAD)
                .aer(AviationAerCorsia.builder()
                    .operatorDetails(AviationCorsiaOperatorDetails.builder().operatorName("operator name").build()
                    )
                    .build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        applySaveActionHandler.process(requestTask.getId(), RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_APPLICATION,
            appUser, taskActionPayload);

        // Verify
        verify(applyService, times(1)).applySaveAction(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(applySaveActionHandler.getTypes()).containsOnly(
            RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_APPLICATION);
    }
}