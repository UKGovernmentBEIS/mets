package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.mapper.PermitSurrenderCessationCompletedCustomMapper;

class PermitSurrenderCessationCompletedCustomMapperTest {

    private final PermitSurrenderCessationCompletedCustomMapper cessationCompletedOperatorMapper =
        new PermitSurrenderCessationCompletedCustomMapper();

    @Test
    void toRequestActionDTO() {
        RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.PERMIT_SURRENDER_CESSATION_COMPLETED)
            .payload(PermitCessationCompletedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD)
                .cessation(PermitCessation.builder().notes("notes").build())
                .build())
            .build();

        RequestActionDTO resultDTO = cessationCompletedOperatorMapper.toRequestActionDTO(requestAction);

        assertEquals(requestAction.getType(), resultDTO.getType());
        PermitCessationCompletedRequestActionPayload resultPayload =
            (PermitCessationCompletedRequestActionPayload) resultDTO.getPayload();
        assertNotNull(resultPayload.getCessation());
        assertNull(resultPayload.getCessation().getNotes());
    }

    @Test
    void getRequestActionType() {
        assertEquals(RequestActionType.PERMIT_SURRENDER_CESSATION_COMPLETED, cessationCompletedOperatorMapper.getRequestActionType());
    }

    @Test
    void getUserRoleTypes() {
        assertThat(cessationCompletedOperatorMapper.getUserRoleTypes())
            .containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}