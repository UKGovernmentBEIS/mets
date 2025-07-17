package uk.gov.pmrv.api.workflow.request.flow.installation.noncompliance.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceCreateActionHandlerTest {

	@InjectMocks
    private NonComplianceCreateActionHandler cut;
    
    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
		
    	final Long accountId = 1L;
		final AppUser appUser = AppUser.builder().userId("user").build();
		final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
		final RequestParams requestParams = RequestParams.builder()
	            .type(RequestType.NON_COMPLIANCE)
	            .accountId(accountId)
	            .requestPayload(NonComplianceRequestPayload.builder()
	                .payloadType(RequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
	                .regulatorAssignee(appUser.getUserId())
	                .build())
	            .build();
		
		when(startProcessRequestService.startProcess(requestParams)).thenReturn(Request.builder().id("reqId").build());
		
		final String result = cut.process(accountId, payload, appUser);
		
		assertThat(result).isEqualTo("reqId");
		
		verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
    
    @Test
    void getRequestCreateActionType() {
    	assertThat(cut.getRequestCreateActionType()).isEqualTo(RequestCreateActionType.NON_COMPLIANCE);
    }
}
