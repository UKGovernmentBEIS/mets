package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PermitVariationGrantedCustomRequestActionMapperTest {

	@InjectMocks
	private PermitVariationGrantedCustomRequestActionMapper cut;

    @Test
    void toRequestActionDTO() {
    	RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.PERMIT_VARIATION_APPLICATION_GRANTED)
    			.payload(PermitVariationApplicationGrantedRequestActionPayload.builder()
    					.determination(PermitVariationGrantDetermination.builder()
    							.reason("reason")
    							.type(DeterminationType.GRANTED)
    							.activationDate(LocalDate.now().plusDays(1))
    							.build())
    					.permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder()
    							.type(ReviewDecisionType.ACCEPTED)
    							.details(PermitAcceptedVariationDecisionDetails.builder()
    									.notes("notes")
    									.build())
    							.build())
    					.decisionNotification(DecisionNotification.builder()
    							.signatory("sign")
    							.build())
    					.officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("offnotice").build())
    					.permitDocument(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("permitdoc").build())
    					.build())
    			.build();
    	
    	RequestActionDTO result = cut.toRequestActionDTO(requestAction);
    	
    	assertThat(result).isNotNull();
    	assertThat(result.getType()).isEqualTo(requestAction.getType());
    	assertThat(result.getPayload()).isInstanceOf(PermitVariationApplicationGrantedRequestActionPayload.class);
    	
    	PermitVariationApplicationGrantedRequestActionPayload resultPayload = (PermitVariationApplicationGrantedRequestActionPayload) result.getPayload();
    	assertThat(resultPayload.getDetermination().getReason()).isNull();
    	assertThat(resultPayload.getDetermination().getType()).isEqualTo(DeterminationType.GRANTED);
    	assertThat(resultPayload.getDetermination().getActivationDate()).isEqualTo(LocalDate.now().plusDays(1));
    }
    
    @Test
    void getUserRoleTypes() {
    	assertThat(cut.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
	
}
