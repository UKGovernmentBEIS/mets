package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper;

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
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceCivilPenaltyApplicationSubmittedCustomMapperTest {

    @InjectMocks
    private NonComplianceCivilPenaltyApplicationSubmittedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED)
            .payload(NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED_PAYLOAD)
                .comments("comments")
                .build())
            .build();

        final RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result.getType()).isEqualTo(requestAction.getType());
        NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload resultPayload =
            (NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getComments()).isNull();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
