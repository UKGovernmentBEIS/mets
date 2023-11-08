package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

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
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;

@ExtendWith(MockitoExtension.class)
public class PermitVariationGrantedPopulateRequestMetadataServiceTest {

	@InjectMocks
    private PermitVariationGrantedPopulateRequestMetadataService cut;

    @Mock
    private RequestService requestService;
    
    @Mock
    private PermitQueryService permitQueryService;
    
    @Test
    void populateRequestMetadata() {
    	String requestId = "requestId";
    	Long accountId = 1L;
    	int permitConsolidationNumber = 2;
    	PermitVariationRequestMetadata requestMetadata = PermitVariationRequestMetadata.builder().build();
    	PermitVariationGrantDetermination grantDetermination = PermitVariationGrantDetermination.builder().logChanges("logChanges").build();
    	PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
    			.determination(grantDetermination)
    			.build();
    	Request request = Request.builder()
				.id(requestId)
				.payload(requestPayload)
				.accountId(accountId)
				.metadata(requestMetadata)
				.build();
    	
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	when(permitQueryService.getPermitConsolidationNumberByAccountId(accountId)).thenReturn(permitConsolidationNumber);
    	
    	cut.populateRequestMetadata(requestId);
    	
    	verify(requestService, times(1)).findRequestById(requestId);
    	verify(permitQueryService, times(1)).getPermitConsolidationNumberByAccountId(accountId);
    	assertThat(requestMetadata.getLogChanges()).isEqualTo(grantDetermination.getLogChanges());
    	assertThat(requestMetadata.getPermitConsolidationNumber()).isEqualTo(permitConsolidationNumber);
    	assertThat(requestPayload.getPermitConsolidationNumber()).isEqualTo(permitConsolidationNumber);
    }
}
