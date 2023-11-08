package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

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
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

@ExtendWith(MockitoExtension.class)
class NerCreateActionHandlerTest {

	@InjectMocks
    private NerCreateActionHandler cut;
    
    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
		
    	final Long accountId = 1L;
    	final RequestCreateActionType type = RequestCreateActionType.NER;
		final PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
		final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
		final RequestParams requestParams = RequestParams.builder()
	            .type(RequestType.NER)
	            .accountId(accountId)
	            .requestPayload(NerRequestPayload.builder()
	                .payloadType(RequestPayloadType.NER_REQUEST_PAYLOAD)
	                .operatorAssignee(pmrvUser.getUserId())
	                .build())
	            .build();
		
		when(startProcessRequestService.startProcess(requestParams)).thenReturn(Request.builder().id("reqId").build());
		
		final String result = cut.process(accountId, type, payload, pmrvUser);
		
		assertThat(result).isEqualTo("reqId");
		
		verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
    
    @Test
    void getType() {
    	assertThat(cut.getType()).isEqualTo(RequestCreateActionType.NER);
    }
}
