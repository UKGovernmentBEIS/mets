package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpWaitForAmendsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowupRequiredChangesDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpWaitForAmendsInitializerTest {

    @InjectMocks
    private PermitNotificationFollowUpWaitForAmendsInitializer initializer;

    @Test
    void initializePayload() {

        final UUID amendFile = UUID.randomUUID();
        final UUID responseFile = UUID.randomUUID();
        final UUID nonRelevantFile = UUID.randomUUID();
        final PermitNotificationFollowUpReviewDecision reviewDecision = PermitNotificationFollowUpReviewDecision.builder()
            .type(PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED)
            .details(
                PermitNotificationFollowupRequiredChangesDecisionDetails.builder()
                    .notes("the notes")
                    .dueDate(LocalDate.of(2023, 1, 1))
                    .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("the changes required", Set.of(amendFile))))
                    .build()
            )
            .build();

        final Map<UUID, String> files = Map.of(amendFile, "amendFile", responseFile, "responseFile", nonRelevantFile, "non-relevant-filename");
        final Request request = Request.builder()
            .type(RequestType.PERMIT_NOTIFICATION)
            .payload(PermitNotificationRequestPayload.builder()
                .reviewDecision(
                    PermitNotificationReviewDecision.builder()
                        .details(
                            PermitNotificationAcceptedDecisionDetails.builder()
                                .officialNotice("officialNotice")
                                .followUp(FollowUp.builder()
                                    .followUpRequest("follow up request")
                                    .followUpResponseExpirationDate(LocalDate.of(2023, 1, 1))
                                    .build())
                                .build()
                        )
                        .build()
                )
                .followUpResponse("follow up response")
                .followUpResponseFiles(Set.of(responseFile))
                .followUpReviewDecision(reviewDecision)
                .followUpResponseAttachments(files)
                .build())
            .build();

        final PermitNotificationFollowUpWaitForAmendsRequestTaskPayload expected =
            PermitNotificationFollowUpWaitForAmendsRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD)
                .followUpRequest("follow up request")
                .followUpResponse("follow up response")
                .followUpFiles(Set.of(responseFile))
                .reviewDecision(reviewDecision)
                .followUpResponseAttachments(Map.of(amendFile, "amendFile", responseFile, "responseFile"))
                .build();

        final RequestTaskPayload actual = initializer.initializePayload(request);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).isEqualTo(Set.of(RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS));
    }
}
