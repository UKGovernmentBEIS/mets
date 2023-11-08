package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReason;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DreRequestPeerReviewValidatorTest {

	@InjectMocks
    private DreRequestPeerReviewValidator cut;

    @Mock
    private DreValidatorService dreValidatorService;
    
    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    
    @Test
    void validate() {
    	Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.regulatorComments("regComments")
						.build())
				.build();
    	RequestTask requestTask = RequestTask.builder()
    			.payload(DreApplicationSubmitRequestTaskPayload.builder()
    					.dre(dre)
    					.build())
    			.build();
    	
    	 PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
    			 .peerReviewer("reviewer")
    			 .build();
    	 
    	 PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
    	 
    	 cut.validate(requestTask, taskActionPayload, pmrvUser);
    	 
    	 verify(peerReviewerTaskAssignmentValidator, times(1)).validate(RequestTaskType.DRE_APPLICATION_PEER_REVIEW, "reviewer", pmrvUser);
    	 verify(dreValidatorService, times(1)).validateDre(dre);
    }
    
}
