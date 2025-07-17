package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.mapper;

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

import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsOperatorDetailsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpVariationUkEtsAmendSubmitMapper extends EmpVariationUkEtsOperatorDetailsMapper {

	@Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewGroupDecisions", source = "payload.reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "empVariationDetailsReviewDecision", source = "payload.empVariationDetailsReviewDecision", qualifiedByName = "variationDetailsReviewDecisionForOperatorAmend")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "reviewAttachments", ignore = true)
    EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload toEmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload(
    		EmpVariationUkEtsApplicationReviewRequestTaskPayload payload, RequestActionPayloadType payloadType);
	
	@AfterMapping
    default void setAmendReviewAttachments(@MappingTarget EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload actionPayload,
    		EmpVariationUkEtsApplicationReviewRequestTaskPayload payload) {
		actionPayload.setReviewAttachments(resolveAmendReviewAttachments(payload.getReviewAttachments(),
				payload.getReviewGroupDecisions(), payload.getEmpVariationDetailsReviewDecision()));
    }
	
	@Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "aviationAccountInfo.serviceContactDetails")
    @Mapping(target = "reviewGroupDecisions", source = "payload.reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "empVariationDetailsReviewDecision", source = "payload.empVariationDetailsReviewDecision", qualifiedByName = "variationDetailsReviewDecisionForOperatorAmend")
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "emissionsMonitoringPlan.empSectionAttachmentIds", ignore = true)
	EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload toEmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload(
			EmpVariationUkEtsRequestPayload payload, RequestAviationAccountInfo aviationAccountInfo,
			RequestTaskPayloadType payloadType);
    
    @AfterMapping
    default void setAmendReviewAttachments(@MappingTarget EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload requestTaskPayload,
    		EmpVariationUkEtsRequestPayload payload) {
        requestTaskPayload.setReviewAttachments(resolveAmendReviewAttachments(payload.getReviewAttachments(),
				payload.getReviewGroupDecisions(), payload.getEmpVariationDetailsReviewDecision()));
    }
    
    private Map<UUID, String> resolveAmendReviewAttachments(
    		Map<UUID, String> reviewAttachments,
			Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions,
			EmpVariationReviewDecision variationDetailsReviewDecision) {
		final Set<UUID> amendFiles = reviewGroupDecisions.values().stream()
            .filter(empReviewDecision -> empReviewDecision.getType() == EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .flatMap(
                empReviewDecision -> ((ChangesRequiredDecisionDetails) empReviewDecision.getDetails()).getRequiredChanges().stream()
                    .map(ReviewDecisionRequiredChange::getFiles))
            .flatMap(Collection::stream).collect(Collectors.toSet());

		final Set<UUID> variationDetailsReviewAttachmentsUuids = new HashSet<>();
        if (variationDetailsReviewDecision != null && EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED.equals(variationDetailsReviewDecision.getType())) {
        	variationDetailsReviewAttachmentsUuids
			.addAll(((ChangesRequiredDecisionDetails) variationDetailsReviewDecision.getDetails())
					.getRequiredChanges().stream()
					.flatMap(change -> change.getFiles().stream())
					.collect(Collectors.toSet()));
        }
        
        final Set<UUID> allAmendFiles = Stream.of(amendFiles, variationDetailsReviewAttachmentsUuids)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    		
		return reviewAttachments.entrySet().stream()
	            .filter(entry -> allAmendFiles.contains(entry.getKey()))
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}  
    
    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> setReviewGroupDecisionsForOperatorAmend(
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecision) {
        return reviewGroupDecision.entrySet().stream()
            .filter(entry -> entry.getValue().getType().equals(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)).map(entry ->
                new AbstractMap.SimpleEntry<>(entry.getKey(), cloneAmendDecisionWithoutNotes(entry.getValue()))
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Named("variationDetailsReviewDecisionForOperatorAmend")
    default EmpVariationReviewDecision setVariationDetailsReviewDecisionForOperatorAmend(
    		EmpVariationReviewDecision empVariationDetailsReviewDecision) {
        return (empVariationDetailsReviewDecision != null
                && empVariationDetailsReviewDecision.getType() == EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                ? cloneAmendDecisionWithoutNotes(empVariationDetailsReviewDecision)
                : null;
    }
    
    private EmpVariationReviewDecision cloneAmendDecisionWithoutNotes(EmpVariationReviewDecision empVariationReviewDecision) {
        return EmpVariationReviewDecision.builder()
                .type(empVariationReviewDecision.getType())
                .details(ChangesRequiredDecisionDetails.builder()
                        .requiredChanges(((ChangesRequiredDecisionDetails) empVariationReviewDecision.getDetails()).getRequiredChanges())
                        .build())
                .build();
    }
}
