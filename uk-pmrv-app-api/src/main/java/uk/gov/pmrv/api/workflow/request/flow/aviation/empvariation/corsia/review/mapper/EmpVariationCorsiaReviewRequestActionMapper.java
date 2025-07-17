package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.mapper;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationRejectedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpVariationCorsiaReviewRequestActionMapper {

	@Mapping(target = "determination.reason", ignore = true)
	@Mapping(target = "fileDocuments", ignore = true)
	@Mapping(target = "empVariationDetailsReviewDecision", source = "empVariationDetailsReviewDecision", qualifiedByName = "approvedVariationDetailsReviewDecisionWithoutNotes")
    @Mapping(target = "reviewGroupDecisions", source = "reviewGroupDecisions", qualifiedByName = "approvedReviewGroupDecisionsWithoutNotes")
    EmpVariationCorsiaApplicationApprovedRequestActionPayload cloneApprovedPayloadIgnoreReasonAndDecisionsNotes(
        EmpVariationCorsiaApplicationApprovedRequestActionPayload payload);
	
	@Named("approvedVariationDetailsReviewDecisionWithoutNotes")
    default EmpVariationReviewDecision setApprovedVariationDetailsReviewDecisionWithoutNotes(
    		EmpVariationReviewDecision variationDetailsReviewDecision) {
    	return cloneGrantedDecisionWithoutNotes(variationDetailsReviewDecision);
    }
    
    @Named("approvedReviewGroupDecisionsWithoutNotes")
    default Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> setApprovedReviewGroupDecisionsWithoutNotes(
            Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions) {
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
    EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload cloneDeemedWithdrawnPayloadIgnoreReason(
        EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload payload);
    
    @Mapping(target = "determination.reason", ignore = true)
    EmpVariationCorsiaApplicationRejectedRequestActionPayload cloneRejectedPayloadIgnoreReason(
        EmpVariationCorsiaApplicationRejectedRequestActionPayload payload);
}
