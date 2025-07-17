package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitVariationRegulatorLedMapper {
	
	@Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD)")
    @Mapping(target = "permitVariationDetailsReviewDecision", source = "permitVariationDetailsReviewDecisionRegulatorLed")
    @Mapping(target = "reviewGroupDecisions", source = "reviewGroupDecisionsRegulatorLed")
    @Mapping(target = "determination", source = "determinationRegulatorLed")
    PermitVariationApplicationRegulatorLedApprovedRequestActionPayload toPermitVariationApplicationRegulatorLedApprovedRequestActionPayload(
        PermitVariationRequestPayload requestPayload);
    
    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "permitVariationDetailsReviewDecision", source = "requestPayload.permitVariationDetailsReviewDecisionRegulatorLed")
    @Mapping(target = "reviewGroupDecisions", source = "requestPayload.reviewGroupDecisionsRegulatorLed")
    @Mapping(target = "determination", source = "requestPayload.determinationRegulatorLed")
    PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload toPermitVariationApplicationSubmitRegulatorLedRequestTaskPayload(
        PermitVariationRequestPayload requestPayload, RequestTaskPayloadType payloadType);

    PermitContainer toPermitContainer(PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload);

    @AfterMapping
    default void setActivationDateFromRegulatorLedTaskPayload(@MappingTarget PermitContainer permitContainer,
    		PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload) {
    	if (requestTaskPayload.getDetermination() != null) {
            permitContainer.setActivationDate(requestTaskPayload.getDetermination().getActivationDate());
        }
    }

    @AfterMapping
    default void setAnnualEmissionsTargetsFromRegulatorLedTaskPayload(@MappingTarget PermitContainer permitContainer,
    		PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload) {
    	if (requestTaskPayload.getDetermination() != null) {
            permitContainer.setAnnualEmissionsTargets(requestTaskPayload.getDetermination().getAnnualEmissionsTargets());
        }
    }
    
    @Mapping(target = "determination.reason", ignore = true)
    @Mapping(target = "fileDocuments", ignore = true)
    @Mapping(target = "permitVariationDetailsReviewDecision.notes", ignore = true)
    @Mapping(target = "reviewGroupDecisions", source = "reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsWithoutNotes")
    PermitVariationApplicationRegulatorLedApprovedRequestActionPayload cloneRegulatorLedApprovedPayloadIgnoreReasonAndDecisionNotes(
    		PermitVariationApplicationRegulatorLedApprovedRequestActionPayload payload);
    
    @Named("reviewGroupDecisionsWithoutNotes")
    default Map<PermitReviewGroup, PermitAcceptedVariationDecisionDetails> setReviewGroupDecisionsWithoutNotes(
            Map<PermitReviewGroup, PermitAcceptedVariationDecisionDetails> reviewGroupDecisions) {
        return reviewGroupDecisions.entrySet().stream()
                .map(entry ->
                        new AbstractMap.SimpleEntry<>(entry.getKey(),
						PermitAcceptedVariationDecisionDetails.builder()
								.variationScheduleItems(entry.getValue().getVariationScheduleItems())
								.build())
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
