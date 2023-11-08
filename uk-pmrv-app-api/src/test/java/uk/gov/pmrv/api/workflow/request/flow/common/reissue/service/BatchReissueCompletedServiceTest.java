package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountReport;

@ExtendWith(MockitoExtension.class)
class BatchReissueCompletedServiceTest {

	@InjectMocks
	private BatchReissueCompletedService cut;

	@Mock
	private RequestService requestService;
	
	@Mock
	private RequestActionUserInfoResolver requestActionUserInfoResolver;
	
	@Test
	void addAction() {
		String requestId = "1";
		BatchReissueRequestPayload requestPayload = BatchReissueRequestPayload.builder()
				.payloadType(RequestPayloadType.PERMIT_BATCH_REISSUE_REQUEST_PAYLOAD)
				.signatory("signatory")
				.build();
		PermitBatchReissueRequestMetadata requestMetadata = PermitBatchReissueRequestMetadata.builder()
				.submitterId("submitterId")
				.submitter("submitter")
				.accountsReports(Map.of(1L, PermitReissueAccountReport.builder()
						.installationName("inst1")
						.build()))
				.build();
		
		Request request = Request.builder()
				.payload(requestPayload)
				.metadata(requestMetadata)
				.type(RequestType.PERMIT_BATCH_REISSUE)
				.build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(requestActionUserInfoResolver.getUserFullName("signatory")).thenReturn("signatoryFullname");
		
		cut.addAction(requestId);
		
		verify(requestService, times(1)).findRequestById(requestId);
		verify(requestActionUserInfoResolver, times(1)).getUserFullName("signatory");
		verify(requestService, times(1)).addActionToRequest(request, 
				BatchReissueCompletedRequestActionPayload.builder()
				.payloadType(RequestActionPayloadType.PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD)
				.submitter("submitter")
				.signatory("signatory")
				.signatoryName("signatoryFullname")
				.numberOfAccounts(1)
				.build(),
				RequestActionType.BATCH_REISSUE_COMPLETED, "submitterId");
	}
}
