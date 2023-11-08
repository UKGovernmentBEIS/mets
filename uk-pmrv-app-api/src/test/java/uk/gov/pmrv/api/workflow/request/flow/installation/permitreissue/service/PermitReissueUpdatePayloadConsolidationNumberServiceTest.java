package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitReissueUpdatePayloadConsolidationNumberServiceTest {

	@InjectMocks
	private PermitReissueUpdatePayloadConsolidationNumberService cut;

	@Mock
	private PermitQueryService permitQueryService;
	
	@Test
	void updateRequestPayloadConsolidationNumber() {
		Long accountId = 1L;
		ReissueRequestPayload payload = ReissueRequestPayload.builder().build();
		Request request = Request.builder()
				.payload(payload)
				.accountId(accountId)
				.build();
		
		when(permitQueryService.getPermitConsolidationNumberByAccountId(accountId)).thenReturn(10);
		
		cut.updateRequestPayloadConsolidationNumber(request);
		
		assertThat(payload.getConsolidationNumber()).isEqualTo(10);
		verify(permitQueryService, times(1)).getPermitConsolidationNumberByAccountId(accountId);
	}
}
