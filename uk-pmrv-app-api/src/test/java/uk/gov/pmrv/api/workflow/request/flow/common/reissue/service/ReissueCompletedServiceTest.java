package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountReport;

@ExtendWith(MockitoExtension.class)
class ReissueCompletedServiceTest {

	@InjectMocks
	private ReissueCompletedService cut;

	@Mock
	private RequestService requestService;
	
	@Test
	void reissueCompleted_not_suceeded() {
		String requestId = "1";
		Long accountId = 2L;
		boolean succeeded = false;
		
		PermitBatchReissueRequestMetadata metadata = PermitBatchReissueRequestMetadata.builder()
			.type(RequestMetadataType.PERMIT_BATCH_REISSUE)
			.accountsReports(Map.of(2L, PermitReissueAccountReport.builder().build()))
			.build();
		
		Request request = Request.builder().metadata(metadata).build();
		
		when(requestService.findRequestById(requestId))
				.thenReturn(request);
		
		cut.reissueCompleted(requestId, accountId, succeeded);
		
		verify(requestService, times(1)).findRequestById(requestId);
		assertThat(metadata.getAccountsReports()).hasSize(1);
		PermitReissueAccountReport report = metadata.getAccountsReports().get(accountId);
		assertThat(report.getIssueDate()).isNull();
		assertThat(report.isSucceeded()).isFalse();
	}
	
	@Test
	void reissueCompleted_suceeded() {
		String requestId = "1";
		Long accountId = 2L;
		boolean succeeded = true;
		
		PermitBatchReissueRequestMetadata metadata = PermitBatchReissueRequestMetadata.builder()
		.type(RequestMetadataType.PERMIT_BATCH_REISSUE)
		.accountsReports(Map.of(2L, PermitReissueAccountReport.builder().build()))
		.build();
		
		Request request = Request.builder().metadata(metadata).build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		
		cut.reissueCompleted(requestId, accountId, succeeded);
		
		verify(requestService, times(1)).findRequestById(requestId);
		verifyNoMoreInteractions(requestService);
		assertThat(metadata.getAccountsReports()).hasSize(1);
		PermitReissueAccountReport report = metadata.getAccountsReports().get(accountId);
		assertThat(report.getIssueDate()).isNotNull();
		assertThat(report.isSucceeded()).isTrue();
	}
}
