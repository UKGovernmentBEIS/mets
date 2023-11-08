package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;

@ExtendWith(MockitoExtension.class)
class ReissueRequestIdGeneratorTest {

	@InjectMocks
	private ReissueRequestIdGenerator cut;

	@Test
	void generate() {
		Long accountId = 12L;
		ReissueRequestMetadata metadata = ReissueRequestMetadata.builder()
    			.submitter("submitter")
    			.submitterId("submitterId")
    			.signatory("signatory")
    			.batchRequestId("permitBatchRequestId")
    			.build();
		
		RequestParams params = RequestParams.builder()
				.accountId(accountId)
				.requestMetadata(metadata)
				.build();
		
		String result = cut.generate(params);
		
		assertThat(result).isEqualTo("B00012-permitBatchRequestId");
	}
	
	@Test
	void getTypes() {
		assertThat(cut.getTypes()).containsExactly(RequestType.PERMIT_REISSUE, RequestType.EMP_REISSUE);
	}
	
	@Test
	void getPrefix() {
		assertThat(cut.getPrefix()).isEqualTo("B");
	}
	
}
