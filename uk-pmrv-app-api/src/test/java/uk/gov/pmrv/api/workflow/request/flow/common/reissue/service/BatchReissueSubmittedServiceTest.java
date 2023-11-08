package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueRequestMetadata;

@ExtendWith(MockitoExtension.class)
class BatchReissueSubmittedServiceTest {

	@InjectMocks
    private BatchReissueSubmittedService cut;
    
    @Mock
    private RequestService requestService;
    
    @Mock
	private RequestActionUserInfoResolver requestActionUserInfoResolver;
    
    @Test
    void batchReissueSubmitted() {
    	String requestId = "1";
    	
    	PermitBatchReissueFilters filters = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.installationCategories(Set.of(InstallationCategory.A))
				.emitterTypes(Set.of(EmitterType.GHGE))
				.build();
    	
    	BatchReissueRequestPayload requestPayload = BatchReissueRequestPayload.builder()
    			.filters(filters)
    			.signatory("signatory")
    			.build();
    	
    	PermitBatchReissueRequestMetadata batchRequestMetadata = PermitBatchReissueRequestMetadata.builder()
    			.submitter("submitter")
    			.submitterId("submitterId")
    			.build();
    	
    	final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .metadata(batchRequestMetadata)
                .type(RequestType.PERMIT_BATCH_REISSUE)
                .build();
    	
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	when(requestActionUserInfoResolver.getUserFullName("signatory")).thenReturn("signatoryFullname");
    	
    	BatchReissueSubmittedRequestActionPayload expectedActionPayload = BatchReissueSubmittedRequestActionPayload.builder()
        		.payloadType(RequestActionPayloadType.PERMIT_BATCH_REISSUE_SUBMITTED_PAYLOAD)
        		.filters(filters)
        		.submitter("submitter")
        		.signatory("signatory")
        		.signatoryName("signatoryFullname")
        		.build();
    	
    	cut.batchReissueSubmitted(requestId);
    	
    	assertThat(request.getSubmissionDate()).isNotNull();
    	assertThat(batchRequestMetadata.getSubmissionDate()).isNotNull();
    	assertThat(request.getSubmissionDate().toLocalDate()).isEqualTo(batchRequestMetadata.getSubmissionDate());
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1)).getUserFullName("signatory");
        verify(requestService, times(1)).addActionToRequest(request, expectedActionPayload, RequestActionType.BATCH_REISSUE_SUBMITTED, "submitterId");
    }
}
