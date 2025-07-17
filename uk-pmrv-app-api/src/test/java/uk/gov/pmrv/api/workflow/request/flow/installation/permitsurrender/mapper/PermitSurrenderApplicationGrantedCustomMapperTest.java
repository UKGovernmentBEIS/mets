package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderApplicationGrantedCustomMapperTest {

    @InjectMocks
    private PermitSurrenderApplicationGrantedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {
        RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.PERMIT_SURRENDER_APPLICATION_GRANTED)
                .payload(PermitSurrenderApplicationGrantedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_GRANTED_PAYLOAD)
                        .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder()
                                .reason("reason")
                                .build())
                        .reviewDecision(PermitSurrenderReviewDecision.builder()
                            .type(PermitSurrenderReviewDecisionType.ACCEPTED)
                            .details(ReviewDecisionDetails.builder().notes("notes").build())
                            .build())
                        .build())
                .build();
        
        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);
        
        assertThat(result.getType()).isEqualTo(requestAction.getType());
        PermitSurrenderApplicationGrantedRequestActionPayload resultPayload = (PermitSurrenderApplicationGrantedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getReviewDetermination().getReason()).isNull();
        assertThat(resultPayload.getReviewDecision()).isNull();
    }
    
    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.PERMIT_SURRENDER_APPLICATION_GRANTED);
    }
    
    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
