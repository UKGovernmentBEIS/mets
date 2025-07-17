package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.NonSignificantChange;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.NonSignificantChangeRelatedChangeType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationType;

@ExtendWith(MockitoExtension.class)
class RequestPermitNotificationReviewServiceTest {

    @InjectMocks
    private RequestPermitNotificationReviewService service;

    @Mock
    private PermitNotificationReviewDecisionValidatorService decisionValidatorService;

    @Test
    void saveReviewDecision() {
        PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload actionPayload = PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .reviewDecision(PermitNotificationReviewDecision.builder()
                        .type(PermitNotificationReviewDecisionType.REJECTED)
                        .details(
                            PermitNotificationAcceptedDecisionDetails.builder()
                                .notes("notes")
                                .officialNotice("official notice reject")
                                .followUp(FollowUp.builder()
                                    .followUpResponseRequired(false)
                                    .build()
                                )
                                .build()
                        )
                        .build())
                .reviewDeterminationCompleted(Boolean.FALSE)
                .build();

        PermitNotificationApplicationReviewRequestTaskPayload taskPayload = PermitNotificationApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD)
                .permitNotification(NonSignificantChange.builder()
                        .type(PermitNotificationType.NON_SIGNIFICANT_CHANGE)
                        .description("description")
                        .relatedChanges(Arrays.asList(
                                NonSignificantChangeRelatedChangeType.MONITORING_METHODOLOGY_PLAN,
                                NonSignificantChangeRelatedChangeType.MONITORING_PLAN
                            )
                        )
                        .build()
                )
                .reviewDecision(PermitNotificationReviewDecision.builder()
                        .type(PermitNotificationReviewDecisionType.ACCEPTED)
                        .details(
                            PermitNotificationAcceptedDecisionDetails.builder()
                                .notes("notes")
                                .officialNotice("official notice")
                                .followUp(FollowUp.builder()
                                    .followUpResponseRequired(false)
                                    .build()
                                )
                                .build()
                        )
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder().id(1L)
                .payload(taskPayload)
                .type(RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_REVIEW)
                .build();

        service.saveReviewDecision(actionPayload, requestTask);
        assertThat(((PermitNotificationApplicationReviewRequestTaskPayload) requestTask.getPayload()).getReviewDecision())
                .isEqualTo(actionPayload.getReviewDecision());
    }

    @Test
    void saveRequestPeerReviewAction() {
        String selectedPeerReview = "selectedPeerReview";
        AppUser appUser = AppUser.builder().userId("regulator").build();
        PermitNotificationApplicationReviewRequestTaskPayload taskPayload = PermitNotificationApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD)
                .reviewDecision(PermitNotificationReviewDecision.builder()
                        .type(PermitNotificationReviewDecisionType.ACCEPTED)
                        .details(
                            PermitNotificationAcceptedDecisionDetails.builder()
                                .notes("notes")
                                .officialNotice("official notice")
                                .followUp(FollowUp.builder()
                                    .followUpResponseRequired(false)
                                    .build()
                                )
                                .build()
                        )
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder().id(1L)
                .request(Request.builder()
                        .payload(PermitNotificationRequestPayload.builder()
                                .payloadType(RequestPayloadType.PERMIT_NOTIFICATION_REQUEST_PAYLOAD)
                                .build())
                        .build())
                .payload(taskPayload)
                .type(RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_REVIEW)
                .build();

        // Invoke
        service.saveRequestPeerReviewAction(requestTask, selectedPeerReview, appUser);

        // Verify
        assertThat(((PermitNotificationApplicationReviewRequestTaskPayload) requestTask.getPayload()).getReviewDecision())
                .isEqualTo(taskPayload.getReviewDecision());
        assertThat(requestTask.getRequest().getPayload().getRegulatorReviewer())
                .isEqualTo(appUser.getUserId());
        assertThat(requestTask.getRequest().getPayload().getRegulatorPeerReviewer())
                .isEqualTo(selectedPeerReview);
    }
}
