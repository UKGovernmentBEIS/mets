package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewCompletedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowupRequiredChangesDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;

import java.util.ArrayList;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitNotificationMapper {

    PermitNotificationContainer toPermitNotificationContainer(PermitNotificationApplicationSubmitRequestTaskPayload payload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "permitNotificationAttachments", ignore = true)
    PermitNotificationApplicationSubmittedRequestActionPayload toApplicationSubmittedRequestActionPayload(
            PermitNotificationApplicationSubmitRequestTaskPayload payload);

    @Mapping(target = "payloadType", source = "payloadType")
    PermitNotificationApplicationReviewRequestTaskPayload toApplicationReviewRequestTaskPayload(
            PermitNotificationRequestPayload requestPayload, RequestTaskPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload toPermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload(
            PermitNotificationRequestPayload requestPayload, RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    PermitNotificationApplicationReviewCompletedDecisionRequestActionPayload toPermitNotificationApplicationReviewCompletedDecisionRequestActionPayload(
            PermitNotificationRequestPayload requestPayload, RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewDecision", source = "payload.reviewDecision", qualifiedByName = "followUpReviewDecisionWithoutNotes")
    PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload cloneCompletedPayloadIgnoreNotes(
            PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload payload, RequestActionPayloadType payloadType);


    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewDecision", source = "payload", qualifiedByName = "extractReviewDecision")
    PermitNotificationApplicationReviewCompletedDecisionRequestActionPayload cloneCompletedPayloadIgnoreNotes(
            PermitNotificationApplicationReviewCompletedDecisionRequestActionPayload payload,
            RequestActionPayloadType payloadType);

    @Named("extractReviewDecision")
    static PermitNotificationReviewDecision extractReviewDecision(PermitNotificationApplicationReviewCompletedDecisionRequestActionPayload payload) {
        PermitNotificationReviewDecision reviewDecision = payload.getReviewDecision();
        reviewDecision.getDetails().setNotes(null);
        return reviewDecision;
    }
    @Named("followUpReviewDecisionWithoutNotes")
    default PermitNotificationFollowUpReviewDecision setReviewDecision(PermitNotificationFollowUpReviewDecision sourceReviewDecision) {
        return cloneFollowUpReviewDecisionIgnoreNotes(sourceReviewDecision);
    }

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewDecision", source = "payload.reviewDecision", qualifiedByName = "reviewDecisionWithoutNotes")
    PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload cloneReviewSubmittedPayloadIgnoreNotes(
            PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload payload, RequestActionPayloadType payloadType);

    @Named("reviewDecisionWithoutNotes")
    default PermitNotificationReviewDecision setReviewDecision(PermitNotificationReviewDecision sourceReviewDecision) {
        ReviewDecisionDetails details;
        if (sourceReviewDecision.getType()== PermitNotificationReviewDecisionType.ACCEPTED) {
            PermitNotificationAcceptedDecisionDetails sourceDetails = (PermitNotificationAcceptedDecisionDetails)sourceReviewDecision.getDetails();
            details = PermitNotificationAcceptedDecisionDetails.builder()
                    .followUp(sourceDetails.getFollowUp())
                    .officialNotice(sourceDetails.getOfficialNotice())
                    .build();
        }
        else {
            PermitNotificationReviewDecisionDetails sourceDetails = (PermitNotificationReviewDecisionDetails)sourceReviewDecision.getDetails();
            details = PermitNotificationReviewDecisionDetails.builder()
                    .officialNotice(sourceDetails.getOfficialNotice())
                    .build();
        }
        return PermitNotificationReviewDecision.builder()
                .details(details)
                .type(sourceReviewDecision.getType())
                .build();
    }

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "decisionDetails.notes", ignore = true)
    PermitNotificationFollowUpReturnedForAmendsRequestActionPayload cloneReturnedForAmendsIgnoreNotes(
            PermitNotificationFollowUpReturnedForAmendsRequestActionPayload payload, RequestActionPayloadType payloadType);

    default PermitNotificationFollowUpReviewDecision cloneFollowUpReviewDecisionIgnoreNotes(PermitNotificationFollowUpReviewDecision source) {
        ReviewDecisionDetails details;
        if (source.getType()== PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED) {
            PermitNotificationFollowupRequiredChangesDecisionDetails sourceDetails = (PermitNotificationFollowupRequiredChangesDecisionDetails) source.getDetails();
            details = PermitNotificationFollowupRequiredChangesDecisionDetails.builder()
                    .dueDate(sourceDetails.getDueDate())
                    .requiredChanges(new ArrayList<>(sourceDetails.getRequiredChanges()))
                    .build();
        } else {
            details = ReviewDecisionDetails.builder().build();
        }

        return PermitNotificationFollowUpReviewDecision.builder()
                .details(details)
                .type(source.getType())
                .build();
    }
}
