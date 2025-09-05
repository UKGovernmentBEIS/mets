package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDetailsAmendedRequestActionPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class NonComplianceDetailsAmendedCustomMapperTest {

    @InjectMocks
    private NonComplianceDetailsAmendedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.NON_COMPLIANCE_DETAILS_AMENDED)
            .payload(NonComplianceDetailsAmendedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.NON_COMPLIANCE_DETAILS_AMENDED_PAYLOAD)
                .comments("comments")
                .build())
            .build();

        final RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result.getType()).isEqualTo(requestAction.getType());
        NonComplianceDetailsAmendedRequestActionPayload resultPayload =
            (NonComplianceDetailsAmendedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getComments()).isNull();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.NON_COMPLIANCE_DETAILS_AMENDED);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
