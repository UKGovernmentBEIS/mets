package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@ExtendWith(MockitoExtension.class)
class ReissueAddCompletedRequestActionServiceTest {

	@InjectMocks
	private ReissueAddCompletedRequestActionService cut;

	@Mock
	private RequestService requestService;
	
	@Mock
	private RequestActionUserInfoResolver requestActionUserInfoResolver;
	
	@Test
	void add() {
		String requestId = "1";
		String signatory = "signatoryUuid";
		FileInfoDTO officialNotice = FileInfoDTO.builder()
    			.name("off").uuid(UUID.randomUUID().toString())
    			.build();
    	FileInfoDTO permitDocument = FileInfoDTO.builder()
    			.name("permitDocument").uuid(UUID.randomUUID().toString())
    			.build();
		ReissueRequestPayload requestPayload = ReissueRequestPayload.builder()
    			.payloadType(RequestPayloadType.REISSUE_REQUEST_PAYLOAD)
    			.officialNotice(officialNotice)
    			.document(permitDocument)
    			.build();
		ReissueRequestMetadata metadata = ReissueRequestMetadata.builder()
    			.submitter("submitter")
    			.submitterId("submitterId")
    			.signatory(signatory)
    			.batchRequestId("batchRequestId")
    			.build();
		Request request = Request.builder()
				.type(RequestType.PERMIT_REISSUE)
				.payload(requestPayload)
				.metadata(metadata)
				.build();
		
		ReissueCompletedRequestActionPayload actionPayload = ReissueCompletedRequestActionPayload.builder()
				.payloadType(RequestActionPayloadType.PERMIT_REISSUE_COMPLETED_PAYLOAD)
				.officialNotice(officialNotice)
				.document(permitDocument)
				.signatory(signatory)
				.signatoryName("signatoryFullname")
				.submitter("submitter")
				.build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(requestActionUserInfoResolver.getUserFullName(signatory)).thenReturn("signatoryFullname");
		
		cut.add(requestId);
		
		verify(requestService, times(1)).findRequestById(requestId);
		verify(requestActionUserInfoResolver, times(1)).getUserFullName(signatory);
		verify(requestService, times(1)).addActionToRequest(request, 
				actionPayload,
				RequestActionType.REISSUE_COMPLETED, 
				"submitterId");
	}
	
}
