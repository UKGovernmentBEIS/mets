package uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AerApplicationCompletedCustomMapperTest {

    @InjectMocks
    private AerApplicationCompletedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {
        RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.AER_APPLICATION_COMPLETED)
                .payload(AerApplicationCompletedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.AER_APPLICATION_COMPLETED_PAYLOAD)
                        .reviewGroupDecisions(Map.of(
                                AerReviewGroup.ADDITIONAL_INFORMATION,
                                AerDataReviewDecision.builder()
                                        .reviewDataType(AerReviewDataType.AER_DATA)
                                        .type(AerDataReviewDecisionType.ACCEPTED)
                                        .build())
                        )
                        .build())
                .build();

        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result.getType()).isEqualTo(requestAction.getType());
        AerApplicationCompletedRequestActionPayload resultPayload = (AerApplicationCompletedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getReviewGroupDecisions()).isEmpty();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.AER_APPLICATION_COMPLETED);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
