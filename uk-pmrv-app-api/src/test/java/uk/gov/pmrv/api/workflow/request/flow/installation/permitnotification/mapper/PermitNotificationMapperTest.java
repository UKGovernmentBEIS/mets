package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowupRequiredChangesDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PermitNotificationMapperTest {
    private final PermitNotificationMapper permitNotificationMapper = Mappers.getMapper(PermitNotificationMapper.class);

    @Test
    void cloneReviewSubmittedPayloadIgnoreNotes_ACCEPTED() {
        PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload actionPayload = PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_GRANTED_PAYLOAD)
                .reviewDecision(PermitNotificationReviewDecision.builder()
                        .type(PermitNotificationReviewDecisionType.ACCEPTED)
                        .details(PermitNotificationAcceptedDecisionDetails.builder()
                                .followUp(FollowUp.builder()
                                        .followUpResponseRequired(true)
                                        .followUpRequest("followUpRequest")
                                        .build())
                                .officialNotice("notice")
                                .notes("note")
                                .build())
                        .build())
                .officialNotice(FileInfoDTO.builder()
                        .name("name")
                        .uuid(UUID.randomUUID().toString())
                        .build())
                .build();

        PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload actionPayloadActual = permitNotificationMapper
                .cloneReviewSubmittedPayloadIgnoreNotes(actionPayload, RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_GRANTED_PAYLOAD);

        assertEquals(actionPayload.getOfficialNotice(), actionPayloadActual.getOfficialNotice());
        assertEquals(((PermitNotificationAcceptedDecisionDetails)actionPayload.getReviewDecision().getDetails()).getFollowUp(),
                ((PermitNotificationAcceptedDecisionDetails)actionPayloadActual.getReviewDecision().getDetails()).getFollowUp());

        assertNull(actionPayloadActual.getReviewDecision().getDetails().getNotes());
    }

    @Test
    void cloneReviewSubmittedPayloadIgnoreNotes_REJECTED() {
        PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload actionPayload = PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_GRANTED_PAYLOAD)
                .reviewDecision(PermitNotificationReviewDecision.builder()
                        .type(PermitNotificationReviewDecisionType.REJECTED)
                        .details(PermitNotificationReviewDecisionDetails.builder()
                                .officialNotice("notice")
                                .notes("note")
                                .build())
                        .build())
                .officialNotice(FileInfoDTO.builder()
                        .name("name")
                        .uuid(UUID.randomUUID().toString())
                        .build())
                .build();

        PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload actionPayloadActual = permitNotificationMapper
                .cloneReviewSubmittedPayloadIgnoreNotes(actionPayload, RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_GRANTED_PAYLOAD);

        assertEquals(actionPayload.getOfficialNotice(), actionPayloadActual.getOfficialNotice());
        assertNull(actionPayloadActual.getReviewDecision().getDetails().getNotes());
    }

    @Test
    void cloneCompletedPayloadIgnoreNotes_ACCEPTED() {
        PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload actionPayload = PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD)
                .reviewDecision(PermitNotificationFollowUpReviewDecision.builder()
                        .type(PermitNotificationFollowUpReviewDecisionType.ACCEPTED)
                        .details(ReviewDecisionDetails.builder()
                                .notes("notes")
                                .build())
                        .build())
                .build();
        PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload actionPayloadActual = permitNotificationMapper
                .cloneCompletedPayloadIgnoreNotes(actionPayload, RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD);

        assertNull(actionPayloadActual.getReviewDecision().getDetails().getNotes());
    }

    @Test
    void cloneCompletedPayloadIgnoreNotes_AMENDS_NEEDED() {
        LocalDate dueDate =LocalDate.now();
        PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload actionPayload = PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD)
                .reviewDecision(PermitNotificationFollowUpReviewDecision.builder()
                        .type(PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED)
                        .details(PermitNotificationFollowupRequiredChangesDecisionDetails.builder()
                                .dueDate(dueDate)
                                .requiredChanges(List.of(ReviewDecisionRequiredChange.builder().reason("reason").build(),
                                        ReviewDecisionRequiredChange.builder().reason("reason2").build()))
                                .notes("notes")
                                .build())
                        .build())
                .build();
        PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload actionPayloadActual = permitNotificationMapper
                .cloneCompletedPayloadIgnoreNotes(actionPayload, RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD);

        assertEquals(((PermitNotificationFollowupRequiredChangesDecisionDetails)actionPayload.getReviewDecision().getDetails()).getDueDate(),
                ((PermitNotificationFollowupRequiredChangesDecisionDetails)actionPayloadActual.getReviewDecision().getDetails()).getDueDate());
        assertEquals(((PermitNotificationFollowupRequiredChangesDecisionDetails)actionPayload.getReviewDecision().getDetails()).getRequiredChanges(),
                ((PermitNotificationFollowupRequiredChangesDecisionDetails)actionPayloadActual.getReviewDecision().getDetails()).getRequiredChanges());
        assertNull(actionPayloadActual.getReviewDecision().getDetails().getNotes());
    }

}