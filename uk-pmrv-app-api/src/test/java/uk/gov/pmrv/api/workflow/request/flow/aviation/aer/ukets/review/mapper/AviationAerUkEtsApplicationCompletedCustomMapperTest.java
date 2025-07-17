package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

class AviationAerUkEtsApplicationCompletedCustomMapperTest {

    private final AviationAerUkEtsApplicationCompletedCustomMapper mapper = new AviationAerUkEtsApplicationCompletedCustomMapper();

    @Test
    void toRequestActionDTO() {
        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        AviationAerUkEtsApplicationCompletedRequestActionPayload requestActionPayload =
            AviationAerUkEtsApplicationCompletedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_COMPLETED_PAYLOAD)
                .reviewGroupDecisions(Map.of(AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, aerDataReviewDecision))
                .build();

        RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.AVIATION_AER_UKETS_APPLICATION_COMPLETED)
            .payload(requestActionPayload)
            .build();

        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        AviationAerUkEtsApplicationCompletedRequestActionPayload resultPayload =
            (AviationAerUkEtsApplicationCompletedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getReviewGroupDecisions()).isEmpty();
    }

    @Test
    void getRequestActionType() {
        assertEquals(RequestActionType.AVIATION_AER_UKETS_APPLICATION_COMPLETED, mapper.getRequestActionType());
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}