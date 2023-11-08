package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.mapper;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitVariationAmendSubmitMapper {
    
	@Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT_PAYLOAD)")
    @Mapping(target = "reviewGroupDecisions", source = "reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "permitVariationDetailsReviewDecision", source = "permitVariationDetailsReviewDecision", qualifiedByName = "variationDetailsReviewDecisionForOperatorAmend")
    @Mapping(target = "reviewAttachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    PermitVariationApplicationAmendsSubmitRequestTaskPayload toPermitVariationAmendsSubmitRequestTaskPayload(
        PermitVariationRequestPayload permitVariationRequestPayload);
	
	@AfterMapping
    default void setAmendReviewAttachments(
        @MappingTarget PermitVariationApplicationAmendsSubmitRequestTaskPayload permitVariationApplicationAmendsSubmitRequestTaskPayload,
        PermitVariationRequestPayload payload) {
		permitVariationApplicationAmendsSubmitRequestTaskPayload
				.setReviewAttachments(resolveAmendReviewAttachments(payload.getReviewAttachments(),
						payload.getReviewGroupDecisions(), payload.getPermitVariationDetailsReviewDecision()));
    }
	
	@Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)")
    @Mapping(target = "reviewGroupDecisions", source = "reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "permitVariationDetailsReviewDecision", source = "permitVariationDetailsReviewDecision", qualifiedByName = "variationDetailsReviewDecisionForOperatorAmend")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "reviewAttachments", ignore = true)
    PermitVariationApplicationReturnedForAmendsRequestActionPayload toPermitVariationApplicationReturnedForAmendsRequestActionPayload(
            PermitVariationApplicationReviewRequestTaskPayload payload);

    @AfterMapping
    default void setAmendReviewAttachments(@MappingTarget PermitVariationApplicationReturnedForAmendsRequestActionPayload actionPayload,
                                           PermitVariationApplicationReviewRequestTaskPayload payload) {
		actionPayload.setReviewAttachments(resolveAmendReviewAttachments(payload.getReviewAttachments(),
				payload.getReviewGroupDecisions(), payload.getPermitVariationDetailsReviewDecision()));
    }
    
	private Map<UUID, String> resolveAmendReviewAttachments(
			Map<UUID, String> reviewAttachments,
			Map<PermitReviewGroup, PermitVariationReviewDecision> reviewGroupDecisions,
			PermitVariationReviewDecision variationDetailsReviewDecision) {
		final Set<UUID> reviewGroupDecisionsAmendFiles = reviewGroupDecisions.values().stream()
	            .filter(permitReviewDecision -> permitReviewDecision.getType() == ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
	            .flatMap(
	                permitReviewDecision -> ((ChangesRequiredDecisionDetails) permitReviewDecision.getDetails()).getRequiredChanges().stream()
	                    .map(ReviewDecisionRequiredChange::getFiles))
	            .flatMap(Collection::stream).collect(Collectors.toSet());
		
		final Set<UUID> variationDetailsReviewAttachmentsUuids = new HashSet<>();
		if (variationDetailsReviewDecision != null
				&& variationDetailsReviewDecision.getType() == ReviewDecisionType.OPERATOR_AMENDS_NEEDED) {
			variationDetailsReviewAttachmentsUuids
					.addAll(((ChangesRequiredDecisionDetails) variationDetailsReviewDecision.getDetails())
							.getRequiredChanges().stream()
							.flatMap(change -> change.getFiles().stream())
							.collect(Collectors.toSet()));
		}
    	
		final Set<UUID> allAmendFiles = Stream.of(reviewGroupDecisionsAmendFiles, variationDetailsReviewAttachmentsUuids)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
		
		return reviewAttachments.entrySet().stream()
	            .filter(entry -> allAmendFiles.contains(entry.getKey()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	@Named("reviewGroupDecisionsForOperatorAmend")
    default Map<PermitReviewGroup, PermitVariationReviewDecision> setReviewGroupDecisionsForOperatorAmend(
            Map<PermitReviewGroup, PermitVariationReviewDecision> reviewGroupDecisions) {
        return reviewGroupDecisions.entrySet().stream()
                .filter(entry -> entry.getValue().getType().equals(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)).map(entry ->
                        new AbstractMap.SimpleEntry<>(entry.getKey(),
                                cloneAmendDecisionWithoutNotes(entry.getValue()))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Named("variationDetailsReviewDecisionForOperatorAmend")
    default PermitVariationReviewDecision setVariationDetailsReviewDecisionForOperatorAmend(
            PermitVariationReviewDecision permitVariationDetailsReviewDecision) {
        return (permitVariationDetailsReviewDecision != null
                && permitVariationDetailsReviewDecision.getType() == ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                ? cloneAmendDecisionWithoutNotes(permitVariationDetailsReviewDecision)
                : null;
    }

    private PermitVariationReviewDecision cloneAmendDecisionWithoutNotes(PermitVariationReviewDecision permitVariationReviewDecision) {
        return PermitVariationReviewDecision.builder()
                .type(permitVariationReviewDecision.getType())
                .details(ChangesRequiredDecisionDetails.builder()
                        .requiredChanges(((ChangesRequiredDecisionDetails) permitVariationReviewDecision.getDetails()).getRequiredChanges())
                        .build())
                .build();
    }
}
