package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRejectDetermination;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitVariationReviewRequestActionMapper {
	
	@Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_GRANTED_PAYLOAD)")
	@Mapping(target = "determination", ignore = true)
    PermitVariationApplicationGrantedRequestActionPayload toPermitVariationApplicationGrantedRequestActionPayload(
        PermitVariationRequestPayload requestPayload);
	
	@AfterMapping
    default void setGrantDetermination(@MappingTarget PermitVariationApplicationGrantedRequestActionPayload grantDeterminationRequestActionPayload,
        PermitVariationRequestPayload requestPayload) {
        if (DeterminationType.GRANTED == requestPayload.getDetermination().getType()) {
            grantDeterminationRequestActionPayload.setDetermination((PermitVariationGrantDetermination) requestPayload.getDetermination());
        }
    }
	
	@Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_REJECTED_PAYLOAD)")
	@Mapping(target = "determination", ignore = true)
    PermitVariationApplicationRejectedRequestActionPayload toPermitVariationApplicationRejectedRequestActionPayload(
        PermitVariationRequestPayload requestPayload);

    @AfterMapping
    default void setRejectedDetermination(@MappingTarget PermitVariationApplicationRejectedRequestActionPayload rejectDeterminationRequestActionPayload,
        PermitVariationRequestPayload requestPayload) {
        if (DeterminationType.REJECTED == requestPayload.getDetermination().getType()) {
            rejectDeterminationRequestActionPayload.setDetermination((PermitVariationRejectDetermination) requestPayload.getDetermination());
        }
    }
	
	@Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD)")
    @Mapping(target = "determination", ignore = true)
    PermitVariationApplicationDeemedWithdrawnRequestActionPayload toPermitVariationApplicationDeemedWithdrawnRequestActionPayload(
        PermitVariationRequestPayload requestPayload);

    @AfterMapping
    default void setDeemedWithdrawnDetermination(
        @MappingTarget PermitVariationApplicationDeemedWithdrawnRequestActionPayload deemedWithdrawnDeterminationRequestActionPayload,
        PermitVariationRequestPayload requestPayload) {
        if (DeterminationType.DEEMED_WITHDRAWN == requestPayload.getDetermination().getType()) {
            deemedWithdrawnDeterminationRequestActionPayload.setDetermination((PermitVariationDeemedWithdrawnDetermination) requestPayload.getDetermination());
        }
    }
	
	@Mapping(target = "determination.reason", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    PermitVariationApplicationRejectedRequestActionPayload cloneRejectedPayloadIgnoreReason(
        PermitVariationApplicationRejectedRequestActionPayload payload);
    
    @Mapping(target = "determination.reason", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    PermitVariationApplicationDeemedWithdrawnRequestActionPayload cloneDeemedWithdrawnPayloadIgnoreReason(
        PermitVariationApplicationDeemedWithdrawnRequestActionPayload payload);

	@Mapping(target = "determination.reason", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    @Mapping(target = "permitVariationDetailsReviewDecision", source = "permitVariationDetailsReviewDecision", qualifiedByName = "grantedVariationDetailsReviewDecisionWithoutNotes")
    @Mapping(target = "reviewGroupDecisions", source = "reviewGroupDecisions", qualifiedByName = "grantedReviewGroupDecisionsWithoutNotes")
    PermitVariationApplicationGrantedRequestActionPayload cloneGrantedPayloadIgnoreReasonAndDecisionsNotes(
        PermitVariationApplicationGrantedRequestActionPayload payload);
    
    @Named("grantedVariationDetailsReviewDecisionWithoutNotes")
    default PermitVariationReviewDecision setGrantedVariationDetailsReviewDecisionWithoutNotes(
    		PermitVariationReviewDecision variationDetailsReviewDecision) {
    	return cloneGrantedDecisionWithoutNotes(variationDetailsReviewDecision);
    }
    
    @Named("grantedReviewGroupDecisionsWithoutNotes")
    default Map<PermitReviewGroup, PermitVariationReviewDecision> setGrantedReviewGroupDecisionsWithoutNotes(
            Map<PermitReviewGroup, PermitVariationReviewDecision> reviewGroupDecisions) {
    	return reviewGroupDecisions.entrySet().stream()
                .map(entry ->
                        new AbstractMap.SimpleEntry<>(entry.getKey(),
                        		cloneGrantedDecisionWithoutNotes(entry.getValue()))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    private PermitVariationReviewDecision cloneGrantedDecisionWithoutNotes(PermitVariationReviewDecision permitVariationReviewDecision) {
		return PermitVariationReviewDecision.builder()
                .type(permitVariationReviewDecision.getType())
                .details(PermitAcceptedVariationDecisionDetails.builder()
                		.variationScheduleItems(((PermitAcceptedVariationDecisionDetails)permitVariationReviewDecision.getDetails()).getVariationScheduleItems())
                		.build())
                .build();
    }
}
