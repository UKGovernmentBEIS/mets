package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpSaveReviewDecisionRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpSaveReviewDecisionActionHandlerTest {

    @InjectMocks
    private PermitNotificationFollowUpSaveReviewDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {

        final PermitNotificationFollowUpReviewDecision decision = PermitNotificationFollowUpReviewDecision.builder()
            .type(PermitNotificationFollowUpReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder()
                .notes("the notes")
                .build())
            .build();
        final PermitNotificationFollowUpSaveReviewDecisionRequestTaskActionPayload actionPayload =
            PermitNotificationFollowUpSaveReviewDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION_PAYLOAD)
                .reviewDecision(decision)
                .reviewSectionsCompleted(Map.of("section", true))
                .build();
        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .payload(PermitNotificationFollowUpApplicationReviewRequestTaskPayload.builder().build())
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION,
            appUser,
            actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());

        assertThat(
            ((PermitNotificationFollowUpApplicationReviewRequestTaskPayload) requestTask.getPayload()).getReviewDecision()).isEqualTo(
            decision);
        assertThat(
            ((PermitNotificationFollowUpApplicationReviewRequestTaskPayload) requestTask.getPayload()).getReviewSectionsCompleted()).isEqualTo(
            Map.of("section", true));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION);
    }
}
