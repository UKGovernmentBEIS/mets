package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.GrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationAcceptedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationEndedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerEndedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerReviewGroupDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.RejectAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerReviewGroup;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface NerMapper {
    
    // ###### REQUEST TASK ###### 
    
    // from request payload to review task payload
    @Mapping(target = "payloadType", source = "payloadType")
    NerApplicationReviewRequestTaskPayload toNerReviewRequestTaskPayload(
        NerRequestPayload nerRequestPayload,
        RequestTaskPayloadType payloadType
    );

    // from request payload to amends task payload
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.NER_APPLICATION_AMENDS_SUBMIT_PAYLOAD)")
    @Mapping(target = "reviewGroupDecisions", source = "reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "reviewAttachments", ignore = true)
    NerApplicationAmendsSubmitRequestTaskPayload toNerApplicationAmendsSubmitRequestTaskPayload(NerRequestPayload requestPayload);

    @AfterMapping
    default void setAmendReviewAttachments(@MappingTarget NerApplicationAmendsSubmitRequestTaskPayload requestTaskPayload,
                                           NerRequestPayload payload) {

        final Set<UUID> amendFiles = requestTaskPayload.getReviewGroupDecisions().values().stream()
            .filter(decision -> decision.getType() == ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .flatMap(
                decision -> ((ChangesRequiredDecisionDetails) decision.getDetails()).getRequiredChanges().stream()
                    .map(ReviewDecisionRequiredChange::getFiles))
            .flatMap(Collection::stream).collect(Collectors.toSet());

        final Map<UUID, String> reviewFiles = payload.getReviewAttachments().entrySet().stream()
            .filter(entry -> amendFiles.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        requestTaskPayload.setReviewAttachments(reviewFiles);
    }

    // ###### REQUEST ACTION ######
    
    // ner submitted
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NER_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "nerAttachments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    NerApplicationSubmittedRequestActionPayload toNerApplicationSubmitted(
        NerApplicationSubmitRequestTaskPayload taskPayload);

    @AfterMapping
    default void setNerAttachments(@MappingTarget NerApplicationSubmittedRequestActionPayload actionPayload,
                                   NerApplicationSubmitRequestTaskPayload taskPayload) {

        actionPayload.setNerAttachments(taskPayload.getNerAttachments());
    }
    
    // ner returned for amends
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NER_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)")
    @Mapping(target = "reviewGroupDecisions", source = "payload.reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "reviewAttachments", ignore = true)
    NerApplicationReturnedForAmendsRequestActionPayload toNerApplicationReturnedForAmendsRequestActionPayload(
        NerApplicationReviewRequestTaskPayload payload
    );

    @AfterMapping
    default void setAmendReviewAttachments(@MappingTarget NerApplicationReturnedForAmendsRequestActionPayload actionPayload,
                                           NerApplicationReviewRequestTaskPayload payload) {
        
        final Set<UUID> amendFiles = actionPayload.getReviewGroupDecisions().values().stream()
            .filter(decision -> decision.getType() == ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .flatMap(
                decision -> ((ChangesRequiredDecisionDetails) decision.getDetails()).getRequiredChanges().stream()
                    .map(ReviewDecisionRequiredChange::getFiles))
            .flatMap(Collection::stream).collect(Collectors.toSet());

        final Map<UUID, String> reviewFiles = payload.getReviewAttachments().entrySet().stream()
            .filter(entry -> amendFiles.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        actionPayload.setReviewAttachments(reviewFiles);
    }

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<NerReviewGroup, NerReviewGroupDecision> setReviewGroupDecisionsForOperatorAmend(
            Map<NerReviewGroup, NerReviewGroupDecision> reviewGroupDecision) {
        
        return reviewGroupDecision.entrySet().stream()
            .filter(entry -> entry.getValue().getType().equals(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)).map(entry ->
                new AbstractMap.SimpleEntry<>(entry.getKey(),
                    NerReviewGroupDecision.builder()
                        .type(entry.getValue().getType())
                        .details(ChangesRequiredDecisionDetails.builder()
                            .requiredChanges(((ChangesRequiredDecisionDetails) entry.getValue().getDetails()).getRequiredChanges()).build())
                        .build())
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // ner ended (closed or deemed withdrawn)
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NER_APPLICATION_ENDED_PAYLOAD)")
    @Mapping(target = "determination", ignore = true)
    NerApplicationEndedRequestActionPayload toNerApplicationEnded(NerRequestPayload requestPayload);

    @AfterMapping
    default void setEndedDetermination(@MappingTarget
                                       NerApplicationEndedRequestActionPayload actionPayload,
                                       NerRequestPayload requestPayload) {

        final NerEndedDetermination determination = (NerEndedDetermination) requestPayload.getDetermination();
        actionPayload.setDetermination(determination);
    }

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "determination.paymentComments", ignore = true)
    NerApplicationEndedRequestActionPayload toNerApplicationEndedIgnorePaymentComments(
        NerApplicationEndedRequestActionPayload actionPayload
    );


    // ner proceeded to authority
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NER_APPLICATION_PROCEEDED_TO_AUTHORITY_PAYLOAD)")
    @Mapping(target = "determination", ignore = true)
    NerApplicationProceededToAuthorityRequestActionPayload toNerApplicationProceededToAuthority(
        NerRequestPayload payload
    );

    @AfterMapping
    default void setProceededToAuthorityDetermination(@MappingTarget
                                                      NerApplicationProceededToAuthorityRequestActionPayload actionPayload,
                                                      NerRequestPayload requestPayload) {

        final NerProceedToAuthorityDetermination determination = (NerProceedToAuthorityDetermination) requestPayload.getDetermination();
        actionPayload.setDetermination(determination);
    }
    
    // ner accepted
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NER_APPLICATION_ACCEPTED_PAYLOAD)")
    @Mapping(target = "authorityResponse", ignore = true)
    NerApplicationAcceptedRequestActionPayload toNerApplicationAccepted(NerRequestPayload requestPayload);

    @AfterMapping
    default void setAcceptedDetermination(@MappingTarget
                                          NerApplicationAcceptedRequestActionPayload actionPayload,
                                          NerRequestPayload requestPayload) {

        final GrantAuthorityResponse authorityResponse = (GrantAuthorityResponse) requestPayload.getAuthorityResponse();
        actionPayload.setAuthorityResponse(authorityResponse);
    }

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "authorityResponse.decisionComments", ignore = true)
    NerApplicationAcceptedRequestActionPayload toNerApplicationAcceptedIgnoreComments(
        NerApplicationAcceptedRequestActionPayload actionPayload
    );

    // ner rejected
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.NER_APPLICATION_REJECTED_PAYLOAD)")
    @Mapping(target = "authorityResponse", ignore = true)
    NerApplicationRejectedRequestActionPayload toNerApplicationRejected(NerRequestPayload requestPayload);

    @AfterMapping
    default void setRejectedDetermination(@MappingTarget
                                          NerApplicationRejectedRequestActionPayload actionPayload,
                                          NerRequestPayload requestPayload) {

        final RejectAuthorityResponse authorityResponse = (RejectAuthorityResponse) requestPayload.getAuthorityResponse();
        actionPayload.setAuthorityResponse(authorityResponse);
    }

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "authorityResponse.decisionComments", ignore = true)
    NerApplicationRejectedRequestActionPayload toNerApplicationRejectedIgnoreComments(
        NerApplicationRejectedRequestActionPayload actionPayload
    );
}
