package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowupRequiredChangesDecisionDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PermitNotificationApplicationCompletedCustomMapperTest {

    @InjectMocks
    private PermitNotificationApplicationCompletedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED)
                .payload(PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD)
                        .reviewDecision(PermitNotificationFollowUpReviewDecision.builder()
                                .type(PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED)
                                .details(
                                        PermitNotificationFollowupRequiredChangesDecisionDetails.builder()
                                                .notes("the notes")
                                                .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("the changes required", Collections.emptySet())))
                                                .build()
                                )
                                .build())
                        .build())
                .build();

        final RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result.getType()).isEqualTo(requestAction.getType());
        final PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload resultPayload =
                (PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getReviewDecision().getDetails().getNotes()).isNull();
        assertThat(((PermitNotificationFollowupRequiredChangesDecisionDetails) resultPayload.getReviewDecision().getDetails()).getRequiredChanges().get(0)
                .getReason()).isEqualTo("the changes required");
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
