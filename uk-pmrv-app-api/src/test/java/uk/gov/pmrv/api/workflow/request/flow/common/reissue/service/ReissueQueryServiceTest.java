package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;

@ExtendWith(MockitoExtension.class)
class ReissueQueryServiceTest {

	@InjectMocks
	private ReissueQueryService cut;

	@Mock
	private RequestService requestService;
	
	@Test
	void getBatchRequest() {
		ReissueRequestMetadata metadata = ReissueRequestMetadata.builder()
    			.batchRequestId("permitBatchRequestId")
    			.build();
		
		Request permitReissueRequest = Request.builder()
				.type(RequestType.PERMIT_REISSUE)
				.metadata(metadata)
				.build();
		
		Request batchRequest = Request.builder()
				.type(RequestType.PERMIT_BATCH_REISSUE)
				.build();
		
		when(requestService.findRequestById("permitBatchRequestId")).thenReturn(batchRequest);
		
		Request result = cut.getBatchRequest(permitReissueRequest);
		
		assertThat(result).isEqualTo(batchRequest);
		verify(requestService, times(1)).findRequestById("permitBatchRequestId");
	}
}
