package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.GrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationAcceptedRequestActionPayload;

@ExtendWith(MockitoExtension.class)
class NerAuthorityResponseAcceptedRequestActionCustomMapperTest {

    @InjectMocks
    private NerAuthorityResponseAcceptedRequestActionCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.NER_APPLICATION_ACCEPTED)
            .payload(NerApplicationAcceptedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.NER_APPLICATION_ACCEPTED_PAYLOAD)
                .authorityResponse(GrantAuthorityResponse.builder()
                    .monitoringMethodologyPlanApproved(true)
                    .decisionComments("decisionComments")
                    .build())
                .build())
            .build();

        final RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result.getType()).isEqualTo(requestAction.getType());
        NerApplicationAcceptedRequestActionPayload resultPayload =
            (NerApplicationAcceptedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getAuthorityResponse().getDecisionComments()).isNull();
        assertThat(resultPayload.getAuthorityResponse().getMonitoringMethodologyPlanApproved()).isNotNull();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.NER_APPLICATION_ACCEPTED);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
