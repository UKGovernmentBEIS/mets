package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.mapper;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationRejectedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpVariationUkEtsReviewRequestActionMapper {

	@Mapping(target = "determination.reason", ignore = true)
	@Mapping(target = "fileDocuments", ignore = true)
	@Mapping(target = "empVariationDetailsReviewDecision", source = "empVariationDetailsReviewDecision", qualifiedByName = "approvedVariationDetailsReviewDecisionWithoutNotes")
    @Mapping(target = "reviewGroupDecisions", source = "reviewGroupDecisions", qualifiedByName = "approvedReviewGroupDecisionsWithoutNotes")
    EmpVariationUkEtsApplicationApprovedRequestActionPayload cloneApprovedPayloadIgnoreReasonAndDecisionsNotes(
        EmpVariationUkEtsApplicationApprovedRequestActionPayload payload);
	
	@Named("approvedVariationDetailsReviewDecisionWithoutNotes")
    default EmpVariationReviewDecision setApprovedVariationDetailsReviewDecisionWithoutNotes(
    		EmpVariationReviewDecision variationDetailsReviewDecision) {
    	return cloneGrantedDecisionWithoutNotes(variationDetailsReviewDecision);
    }
    
    @Named("approvedReviewGroupDecisionsWithoutNotes")
    default Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> setApprovedReviewGroupDecisionsWithoutNotes(
            Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions) {
    	return reviewGroupDecisions.entrySet().stream()
                .map(entry ->
                        new AbstractMap.SimpleEntry<>(entry.getKey(),
                        		cloneGrantedDecisionWithoutNotes(entry.getValue()))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    private EmpVariationReviewDecision cloneGrantedDecisionWithoutNotes(EmpVariationReviewDecision variationReviewDecision) {
		return EmpVariationReviewDecision.builder()
                .type(variationReviewDecision.getType())
                .details(EmpAcceptedVariationDecisionDetails.builder()
                		.variationScheduleItems(((EmpAcceptedVariationDecisionDetails)variationReviewDecision.getDetails()).getVariationScheduleItems())
                		.build())
                .build();
    }

    @Mapping(target = "determination.reason", ignore = true)
    EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload cloneDeemedWithdrawnPayloadIgnoreReason(
        EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload payload);
    
    @Mapping(target = "determination.reason", ignore = true)
    EmpVariationUkEtsApplicationRejectedRequestActionPayload cloneRejectedPayloadIgnoreReason(
        EmpVariationUkEtsApplicationRejectedRequestActionPayload payload);
}
