package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;

@ExtendWith(MockitoExtension.class)
class PermitVariationRequestQueryServiceTest {

	@InjectMocks
    private PermitVariationRequestQueryService cut;

    @Mock
    private RequestRepository requestRepository;

    @Test
    void findPermitVariationRequests() {
    	Long accountId = 1L;
    	PermitVariationRequestMetadata permitVariationRequestMetadata1 = PermitVariationRequestMetadata.builder()
    			.type(RequestMetadataType.PERMIT_VARIATION)
    			.logChanges("log1")
    			.build();
    	PermitVariationRequestMetadata permitVariationRequestMetadata2 = PermitVariationRequestMetadata.builder()
    			.type(RequestMetadataType.PERMIT_VARIATION)
    			.logChanges("log2")
    			.build();

    	LocalDateTime request1EndDate = LocalDateTime.now();
    	LocalDateTime request2EndDate = LocalDateTime.now();
    	List<Request> requests = List.of(
    			Request.builder().id("request1").status(RequestStatus.APPROVED)
    				.endDate(request1EndDate)
    				.submissionDate(request1EndDate)
	    			.accountId(accountId)
	    			.type(RequestType.PERMIT_VARIATION)
	    			.metadata(permitVariationRequestMetadata1)
	    			.build(),
    			Request.builder().id("request2").status(RequestStatus.APPROVED)
    				.endDate(request2EndDate)
    				.submissionDate(request2EndDate)
	    			.accountId(accountId)
	    			.type(RequestType.PERMIT_VARIATION)
	    			.metadata(permitVariationRequestMetadata2)
	    			.build()
    			);

		when(requestRepository.findByAccountIdAndTypeAndStatus(accountId, RequestType.PERMIT_VARIATION,
			RequestStatus.APPROVED, Sort.by(Sort.Direction.ASC, "creationDate")))
			.thenReturn(requests);

		List<PermitVariationRequestInfo> results = cut.findPermitVariationRequests(accountId);
		assertThat(results).containsExactlyInAnyOrder(
				PermitVariationRequestInfo.builder().endDate(request1EndDate).submissionDate(request1EndDate)
						.id("request1").metadata(permitVariationRequestMetadata1).build(),
				PermitVariationRequestInfo.builder().endDate(request2EndDate).submissionDate(request2EndDate)
						.id("request2").metadata(permitVariationRequestMetadata2).build());
		verify(requestRepository, times(1)).findByAccountIdAndTypeAndStatus(accountId,
				RequestType.PERMIT_VARIATION, RequestStatus.APPROVED, Sort.by(Sort.Direction.ASC, "creationDate"));
    }

	@Test
	void findApprovedPermitVariationRequests() {
		Long accountId = 1L;
		PermitVariationRequestMetadata permitVariationRequestMetadata1 = PermitVariationRequestMetadata.builder()
				.type(RequestMetadataType.PERMIT_VARIATION)
				.logChanges("log1")
				.build();
		PermitVariationRequestMetadata permitVariationRequestMetadata2 = PermitVariationRequestMetadata.builder()
				.type(RequestMetadataType.PERMIT_VARIATION)
				.logChanges("log2")
				.build();

		LocalDateTime requestEndDateCurrent = LocalDateTime.now();
		List<Request> requests = List.of(
				Request.builder().id("request1").status(RequestStatus.APPROVED)
						.endDate(requestEndDateCurrent)
						.submissionDate(requestEndDateCurrent)
						.accountId(accountId)
						.type(RequestType.PERMIT_VARIATION)
						.metadata(permitVariationRequestMetadata1)
						.build(),
				Request.builder().id("request2").status(RequestStatus.APPROVED)
						.endDate(requestEndDateCurrent)
						.submissionDate(requestEndDateCurrent)
						.accountId(accountId)
						.type(RequestType.PERMIT_VARIATION)
						.metadata(permitVariationRequestMetadata2)
						.build()
		);

		when(requestRepository.findByAccountIdAndTypeAndStatusOrderByEndDateDesc(accountId,
				RequestType.PERMIT_VARIATION, RequestStatus.APPROVED)).thenReturn(requests);

		List<PermitVariationRequestInfo> results = cut.findApprovedPermitVariationRequests(accountId);
		assertThat(results).containsExactly(
				PermitVariationRequestInfo.builder().endDate(requestEndDateCurrent).submissionDate(requestEndDateCurrent)
						.id("request1").metadata(permitVariationRequestMetadata1).build(),
				PermitVariationRequestInfo.builder().endDate(requestEndDateCurrent).submissionDate(requestEndDateCurrent)
						.id("request2").metadata(permitVariationRequestMetadata2).build());
		verify(requestRepository, times(1)).findByAccountIdAndTypeAndStatusOrderByEndDateDesc(accountId,
				RequestType.PERMIT_VARIATION, RequestStatus.APPROVED);
	}
}
