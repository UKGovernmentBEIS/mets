package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDetermination;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceFinalDeterminationApplicationSubmittedCustomMapperTest {

    @InjectMocks
    private NonComplianceFinalDeterminationApplicationSubmittedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED)
            .payload(NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED_PAYLOAD)
                .finalDetermination(NonComplianceFinalDetermination.builder().comments("comments").build())
                .build())
            .build();

        final RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result.getType()).isEqualTo(requestAction.getType());
        NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload resultPayload =
            (NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getFinalDetermination().getComments()).isNull();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
