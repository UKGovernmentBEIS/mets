package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpContactUpdateServiceTest {

    @InjectMocks
    private EmpContactUpdateService empContactUpdateService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void updateEmpApplicationSubmitTaskPayloadWithServiceContact() {

        Long accountId = 1L;
        Set<RequestTaskType> taskTypes = Set.of(
            RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT, RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT
        );
        RequestTaskPayload taskPayload = EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder()
                .type(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT)
                .request(Request.builder().accountId(accountId).build())
                .payload(taskPayload)
                .build();
        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().build();
        when(requestTaskService.findTasksByTypeInAndAccountId(taskTypes, accountId)).thenReturn(List.of(requestTask));
        empContactUpdateService.updateEmpApplicationSubmitTaskPayloadWithServiceContactDetails(accountId, serviceContactDetails);
        verify(requestTaskService, times(1)).findTasksByTypeInAndAccountId(taskTypes, accountId);
        verify(requestTaskService, times(1)).updateRequestTaskPayload(requestTask, taskPayload);
    }

    @Test
    void updateEmpApplicationSubmitTaskPayloadWithServiceContact_no_task_found() {

        Long accountId = 1L;
        Set<RequestTaskType> taskTypes = Set.of(
            RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT, RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT
        );

        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().build();
        when(requestTaskService.findTasksByTypeInAndAccountId(taskTypes, accountId)).thenReturn(List.of());
        empContactUpdateService.updateEmpApplicationSubmitTaskPayloadWithServiceContactDetails(accountId, serviceContactDetails);
        verify(requestTaskService, times(1)).findTasksByTypeInAndAccountId(taskTypes, accountId);
        verifyNoMoreInteractions(requestTaskService);
    }
}
