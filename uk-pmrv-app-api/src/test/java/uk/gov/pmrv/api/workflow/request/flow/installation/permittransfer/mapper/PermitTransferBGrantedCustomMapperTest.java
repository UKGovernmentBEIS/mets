package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBDetailsConfirmationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBDetailsConfirmationReviewDecisionType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PermitTransferBGrantedCustomMapperTest {

    @InjectMocks
    private PermitTransferBGrantedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.PERMIT_TRANSFER_B_APPLICATION_GRANTED)
            .payload(PermitTransferBApplicationGrantedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_TRANSFER_B_APPLICATION_GRANTED_PAYLOAD)
                .determination(PermitIssuanceGrantDetermination.builder()
                    .reason("reason")
                    .build())
                .permitTransferDetailsConfirmationDecision(PermitTransferBDetailsConfirmationReviewDecision.builder()
                    .type(PermitTransferBDetailsConfirmationReviewDecisionType.ACCEPTED)
                    .build())
                .officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("name").build())
                .permitDocument(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("name").build())
                .build())
            .build();

        final RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result.getType()).isEqualTo(requestAction.getType());
        PermitTransferBApplicationGrantedRequestActionPayload resultPayload =
            (PermitTransferBApplicationGrantedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getDetermination().getReason()).isNull();
        assertThat(resultPayload.getPermitTransferDetailsConfirmationDecision()).isNull();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.PERMIT_TRANSFER_B_APPLICATION_GRANTED);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
