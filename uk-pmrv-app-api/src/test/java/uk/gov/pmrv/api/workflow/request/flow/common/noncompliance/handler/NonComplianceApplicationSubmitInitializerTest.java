package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceApplicationSubmitInitializerTest {

    @InjectMocks
    private NonComplianceApplicationSubmitInitializer initializer;
    
    @Mock
    private RequestRepository requestRepository;

    @Test
    void initializePayload() {

        final long accountId = 1L;
        final Request request = Request.builder().accountId(accountId).build();

        final List<RequestInfoDTO> availableRequests =
            List.of(RequestInfoDTO.builder().id("req1").build(), RequestInfoDTO.builder().id("req2").build());
        
        when(requestRepository.findAllByAccountIdAndTypeNotIn(accountId, 
            List.of(RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestType.NON_COMPLIANCE, RequestType.AVIATION_NON_COMPLIANCE)))
            .thenReturn(availableRequests);
        
        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(requestTaskPayload, NonComplianceApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_APPLICATION_SUBMIT_PAYLOAD)
            .availableRequests(availableRequests)
            .build());
    }

    @Test
    void getRequestTaskTypes() {

        assertThat(initializer.getRequestTaskTypes()).containsExactlyInAnyOrder(RequestTaskType.NON_COMPLIANCE_APPLICATION_SUBMIT,
            RequestTaskType.AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT);
    }

}
