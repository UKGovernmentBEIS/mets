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

import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueRequestMetadata;

@ExtendWith(MockitoExtension.class)
class ReissueCreateRequestServiceTest {

	@InjectMocks
	private ReissueCreateRequestService cut;

	@Mock
	private RequestService requestService;
	
	@Mock
	private StartProcessRequestService startProcessRequestService;
	
	@Test
	void createReissueRequest() {
		Long accountId = 1L;
		String batchRequestId = "batchRequestId";
		String batchRequestBusinessKey = "batchRequestBusinessKey";
		
		BatchReissueRequestPayload batchRequestPayload = BatchReissueRequestPayload.builder()
				.payloadType(RequestPayloadType.PERMIT_BATCH_REISSUE_REQUEST_PAYLOAD)
				.signatory("signatory")
				.build();
		
		PermitBatchReissueRequestMetadata batchRequestMetadata = PermitBatchReissueRequestMetadata.builder()
				.submitterId("submitterId")
				.submitter("submitter")
				.build();
		
		Request batchRequest = Request.builder()
				.payload(batchRequestPayload)
				.metadata(batchRequestMetadata)
				.type(RequestType.PERMIT_BATCH_REISSUE)
				.build();
		
		when(requestService.findRequestById(batchRequestId)).thenReturn(batchRequest);
		
		cut.createReissueRequest(accountId, batchRequestId, batchRequestBusinessKey);
		
		verify(requestService, times(1)).findRequestById(batchRequestId);
		verify(startProcessRequestService, times(1)).startProcess(RequestParams.builder()
				.type(RequestType.PERMIT_REISSUE)
				.accountId(accountId)
				.requestPayload(ReissueRequestPayload.builder()
						.payloadType(RequestPayloadType.REISSUE_REQUEST_PAYLOAD)
						.build())
				.requestMetadata(ReissueRequestMetadata.builder()
						.type(RequestMetadataType.REISSUE)
						.batchRequestId(batchRequestId)
						.signatory(batchRequestPayload.getSignatory())
						.submitterId(batchRequestMetadata.getSubmitterId())
						.submitter(batchRequestMetadata.getSubmitter())
						.build())
				.processVars(Map.of(
						BpmnProcessConstants.BATCH_REQUEST_BUSINESS_KEY, batchRequestBusinessKey,
						BpmnProcessConstants.ACCOUNT_ID, accountId
						))
				.build());
	}
}
