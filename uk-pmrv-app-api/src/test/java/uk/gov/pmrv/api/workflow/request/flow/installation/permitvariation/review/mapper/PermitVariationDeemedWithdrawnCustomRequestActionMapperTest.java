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
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationDeemedWithdrawnDetermination;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PermitVariationDeemedWithdrawnCustomRequestActionMapperTest {

	@InjectMocks
	private PermitVariationDeemedWithdrawnCustomRequestActionMapper cut;

    @Test
    void toRequestActionDTO() {
    	RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN)
    			.payload(PermitVariationApplicationDeemedWithdrawnRequestActionPayload.builder()
    					.determination(PermitVariationDeemedWithdrawnDetermination.builder()
    							.reason("reason")
    							.type(DeterminationType.DEEMED_WITHDRAWN)
    							.build())
    					.decisionNotification(DecisionNotification.builder()
    							.signatory("sign")
    							.build())
    					.officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("offnotice").build())
    					.build())
    			.build();
    	
    	RequestActionDTO result = cut.toRequestActionDTO(requestAction);
    	
    	assertThat(result).isNotNull();
    	assertThat(result.getType()).isEqualTo(requestAction.getType());
    	assertThat(result.getPayload()).isInstanceOf(PermitVariationApplicationDeemedWithdrawnRequestActionPayload.class);
    	
    	PermitVariationApplicationDeemedWithdrawnRequestActionPayload resultPayload = (PermitVariationApplicationDeemedWithdrawnRequestActionPayload) result.getPayload();
    	assertThat(resultPayload.getDetermination().getReason()).isNull();
    	assertThat(resultPayload.getDetermination().getType()).isEqualTo(DeterminationType.DEEMED_WITHDRAWN);
    }
    
    @Test
    void getUserRoleTypes() {
    	assertThat(cut.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
	
}
