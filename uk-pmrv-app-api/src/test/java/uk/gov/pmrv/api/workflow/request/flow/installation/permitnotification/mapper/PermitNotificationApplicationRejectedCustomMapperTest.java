package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.mapper;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionDetails;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class PermitNotificationApplicationRejectedCustomMapperTest {
    @InjectMocks
    private PermitNotificationApplicationRejectedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        PermitNotificationReviewDecisionDetails permitNotificationReviewDecisionDetails = PermitNotificationReviewDecisionDetails.builder()
                .notes("the notes")
                .officialNotice("officialNotice")
                .build();
        final RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.PERMIT_NOTIFICATION_APPLICATION_REJECTED)
                .payload(PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_REJECTED_PAYLOAD)
                        .reviewDecision(PermitNotificationReviewDecision.builder()
                                .details(permitNotificationReviewDecisionDetails)
                                .build())
                        .officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("name").build())
                        .build())
                .build();

        final RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result.getType()).isEqualTo(requestAction.getType());
        final PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload resultPayload =
                (PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getReviewDecision().getDetails().getNotes()).isNull();
        assertEquals(permitNotificationReviewDecisionDetails.getOfficialNotice(), ((PermitNotificationReviewDecisionDetails)resultPayload.getReviewDecision().getDetails()).getOfficialNotice());
        assertNull(resultPayload.getReviewDecision().getDetails().getNotes());
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.PERMIT_NOTIFICATION_APPLICATION_REJECTED);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }

}