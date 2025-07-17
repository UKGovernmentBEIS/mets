package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaApplicationSkippedCustomMapperTest {
    
    @InjectMocks
    private AviationAerCorsiaApplicationSkippedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {
        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        AviationAerCorsiaApplicationCompletedRequestActionPayload requestActionPayload =
            AviationAerCorsiaApplicationCompletedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_COMPLETED_PAYLOAD)
                .reviewGroupDecisions(Map.of(AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, aerDataReviewDecision))
                .build();

        RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.AVIATION_AER_CORSIA_APPLICATION_REVIEW_SKIPPED)
            .payload(requestActionPayload)
            .build();

        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        AviationAerCorsiaApplicationCompletedRequestActionPayload resultPayload =
            (AviationAerCorsiaApplicationCompletedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getReviewGroupDecisions()).isEmpty();
    }

    @Test
    void getRequestActionType() {
        assertEquals(RequestActionType.AVIATION_AER_CORSIA_APPLICATION_REVIEW_SKIPPED, mapper.getRequestActionType());
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}