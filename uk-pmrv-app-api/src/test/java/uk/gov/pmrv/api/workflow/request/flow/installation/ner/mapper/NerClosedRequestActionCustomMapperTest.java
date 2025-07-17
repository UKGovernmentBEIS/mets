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
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationEndedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerEndedDetermination;

@ExtendWith(MockitoExtension.class)
class NerClosedRequestActionCustomMapperTest {

    @InjectMocks
    private NerClosedRequestActionCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.NER_APPLICATION_CLOSED)
            .payload(NerApplicationEndedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.NER_APPLICATION_ENDED_PAYLOAD)
                .determination(NerEndedDetermination.builder()
                    .reason("reason")
                    .paymentComments("paymentComments")
                    .build())
                .build())
            .build();

        final RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result.getType()).isEqualTo(requestAction.getType());
        NerApplicationEndedRequestActionPayload resultPayload =
            (NerApplicationEndedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getDetermination().getPaymentComments()).isNull();
        assertThat(resultPayload.getDetermination().getReason()).isNotNull();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.NER_APPLICATION_CLOSED);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
