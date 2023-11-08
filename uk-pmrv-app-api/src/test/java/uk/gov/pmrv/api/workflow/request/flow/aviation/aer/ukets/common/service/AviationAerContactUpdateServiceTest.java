package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerContactUpdateService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerContactUpdateServiceTest {

    @InjectMocks
    private AviationAerContactUpdateService contactUpdateService;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Test
    void updateAerAviationApplicationSubmitTaskPayloadWithServiceContact() {

        Long accountId = 1L;
        RequestTaskType taskType = RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT;
        RequestTaskPayload taskPayload = AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder()
                .type(taskType)
                .request(Request.builder().accountId(accountId).build())
                .payload(taskPayload)
                .build();
        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().build();
        when(requestTaskService.findTasksByTypeInAndAccountId(
            Set.of(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT, RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT), accountId))
            .thenReturn(List.of(requestTask));
        contactUpdateService.updateAerAviationApplicationSubmitTaskPayloadWithServiceContactDetails(accountId, serviceContactDetails);
        verify(requestTaskService, times(1))
            .findTasksByTypeInAndAccountId(Set.of(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT, 
                RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT), accountId);
        verify(requestTaskService, times(1)).updateRequestTaskPayload(requestTask, taskPayload);
    }

    @Test
    void updateAerAviationApplicationSubmitTaskPayloadWithServiceContact_no_task_found() {

        Long accountId = 1L;

        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().build();
        when(requestTaskService.findTasksByTypeInAndAccountId(
            Set.of(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT, RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT), accountId)
        ).thenReturn(List.of());
        contactUpdateService.updateAerAviationApplicationSubmitTaskPayloadWithServiceContactDetails(accountId, serviceContactDetails);
        verify(requestTaskService, times(1)).findTasksByTypeInAndAccountId(
            Set.of(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT, RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT), 
            accountId);
        verifyNoMoreInteractions(requestTaskService);
    }
}
