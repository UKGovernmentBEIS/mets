package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

import java.time.LocalDateTime;
import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DreCreateActionHandlerTest {

	@InjectMocks
    private DreCreateActionHandler cut;

    @Mock
    private RequestQueryService requestQueryService;
    
    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
    	Long accountId = 1L;
    	String aerRequestId = "aerRequestId";
    	Year year = Year.of(2023);
    	ReportRelatedRequestCreateActionPayload payload = ReportRelatedRequestCreateActionPayload.builder()
    			.requestId(aerRequestId)
    			.build();
		AppUser appUser = AppUser.builder().userId("user").build();
		
		LocalDateTime initiatorRequestSubmissionDate = LocalDateTime.now();
		AerInitiatorRequest initiatorRequest = AerInitiatorRequest.builder().type(RequestType.PERMIT_REVOCATION).submissionDateTime(initiatorRequestSubmissionDate).build();
		RequestDetailsDTO aerRequestDetails = new RequestDetailsDTO(aerRequestId, RequestType.AER,
				RequestStatus.IN_PROGRESS, LocalDateTime.now(), 
				AerRequestMetadata.builder().year(year).initiatorRequest(initiatorRequest).build());
		
		RequestParams requestParams = RequestParams.builder()
	            .type(RequestType.DRE)
	            .accountId(accountId)
	            .requestMetadata(DreRequestMetadata.builder()
	            		.type(RequestMetadataType.DRE)
	            		.year(((AerRequestMetadata)aerRequestDetails.getRequestMetadata()).getYear())
	            		.build())
	            .requestPayload(DreRequestPayload.builder()
	                .payloadType(RequestPayloadType.DRE_REQUEST_PAYLOAD)
	                .regulatorAssignee(appUser.getUserId())
	                .reportingYear(year)
	                .initiatorRequest(initiatorRequest)
	                .build())
	            .build();
		
		when(requestQueryService.findRequestDetailsById(aerRequestId)).thenReturn(aerRequestDetails);
		when(startProcessRequestService.startProcess(requestParams)).thenReturn(Request.builder().id("dreId").build());
		
		String result = cut.process(accountId, payload, appUser);
		
		assertThat(result).isEqualTo("dreId");
		verify(requestQueryService, times(1)).findRequestDetailsById(aerRequestId);
		verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
    
    @Test
    void getRequestCreateActionType() {
    	assertThat(cut.getRequestCreateActionType()).isEqualTo(RequestCreateActionType.DRE);
    }
}
