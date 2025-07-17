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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationDeemWithdraw;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderApplicationDeemedWithdrawnCustomMapperTest {

    @InjectMocks
    private PermitSurrenderApplicationDeemedWithdrawnCustomMapper mapper;

    @Test
    void toRequestActionDTO() {
        RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN)
                .payload(PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD)
                        .reviewDetermination(PermitSurrenderReviewDeterminationDeemWithdraw.builder()
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
        PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload resultPayload = (PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getReviewDetermination().getReason()).isNull();
        assertThat(resultPayload.getReviewDecision()).isNull();
    }
    
    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN);
    }
    
    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
