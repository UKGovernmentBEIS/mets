package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.service.RequestEmpUkEtsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsApplySaveActionHandlerTest {

    @InjectMocks
    private EmpIssuanceUkEtsApplySaveActionHandler handler;

    @Mock
    private RequestEmpUkEtsService requestEmpUkEtsService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        handler.process(requestTask.getId(), RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION, appUser, requestTaskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestEmpUkEtsService, times(1)).applySaveAction(requestTaskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsOnly(RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION);
    }
}