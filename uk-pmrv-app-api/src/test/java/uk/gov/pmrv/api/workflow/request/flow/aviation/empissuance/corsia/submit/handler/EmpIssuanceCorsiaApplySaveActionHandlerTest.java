package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.service.RequestEmpCorsiaService;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaApplySaveActionHandlerTest {

    @InjectMocks
    private EmpIssuanceCorsiaApplySaveActionHandler handler;

    @Mock
    private RequestEmpCorsiaService requestEmpCorsiaService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_PAYLOAD)
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        handler.process(requestTask.getId(), RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION, pmrvUser, requestTaskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestEmpCorsiaService, times(1)).applySaveAction(requestTaskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsOnly(RequestTaskActionType.EMP_ISSUANCE_CORSIA_SAVE_APPLICATION);
    }
}