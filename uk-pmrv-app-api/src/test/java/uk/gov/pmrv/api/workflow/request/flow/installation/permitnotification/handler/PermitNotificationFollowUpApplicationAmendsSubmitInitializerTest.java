package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.OtherFactor;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowupRequiredChangesDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler.PermitNotificationFollowUpApplicationAmendsSubmitInitializer;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpApplicationAmendsSubmitInitializerTest {

    @InjectMocks
    private PermitNotificationFollowUpApplicationAmendsSubmitInitializer initializer;

    @Test
    void initializePayload() {

        final PermitNotificationFollowUpReviewDecision reviewDecision =
            PermitNotificationFollowUpReviewDecision.builder()
                .type(PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED)
                .details(
                    PermitNotificationFollowupRequiredChangesDecisionDetails.builder()
                        .notes("the notes")
                        .dueDate(LocalDate.of(2023, 1, 1))
                        .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("the changes required", Collections.emptySet())))
                        .build()
                )
                .build();

        final UUID file = UUID.randomUUID();
        final Request request = Request.builder()
            .type(RequestType.PERMIT_NOTIFICATION)
            .payload(PermitNotificationRequestPayload.builder()
                .reviewDecision(PermitNotificationReviewDecision.builder()
                    .type(PermitNotificationReviewDecisionType.ACCEPTED)
                    .details(
                        PermitNotificationAcceptedDecisionDetails.builder()
                            .officialNotice("officialNotice")
                            .notes("notes")
                            .followUp(FollowUp.builder()
                                .followUpRequest("the request")
                                .followUpResponseExpirationDate(LocalDate.of(2023, 1, 1))
                                .build())
                            .build()
                    )
                    .build())
                .followUpResponse("the response")
                .followUpReviewDecision(reviewDecision)
                .followUpResponseFiles(Set.of(file))
                .followUpResponseAttachments(Map.of(file, "filename"))
                .permitNotification(OtherFactor.builder().type(PermitNotificationType.OTHER_FACTOR).build())
                .followUpResponseSubmissionDate(LocalDate.of(2022, 1, 1))
                .followUpReviewSectionsCompleted(Map.of("section", true))
                .build())
            .build();

        final PermitNotificationFollowUpReviewDecision expectedReviewDecision =
            PermitNotificationFollowUpReviewDecision.builder()
                .type(PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED)
                .details(
                    PermitNotificationFollowupRequiredChangesDecisionDetails.builder()
                        .notes(null)
                        .dueDate(LocalDate.of(2023, 1, 1))
                        .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("the changes required", Collections.emptySet())))
                        .build()
                )
                .build();
        final PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload expected =
            PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .followUpRequest("the request")
                .followUpResponseExpirationDate(LocalDate.of(2023, 1, 1))
                .followUpResponse("the response")
                .followUpFiles(Set.of(file))
                .followUpAttachments(Map.of(file, "filename"))
                .permitNotificationType(PermitNotificationType.OTHER_FACTOR)
                .submissionDate(LocalDate.of(2022, 1, 1))
                .reviewDecision(expectedReviewDecision)
                .reviewSectionsCompleted(Map.of("section", true))
                .build();

        final RequestTaskPayload actual = initializer.initializePayload(request);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).isEqualTo(Set.of(RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT));
    }
}
