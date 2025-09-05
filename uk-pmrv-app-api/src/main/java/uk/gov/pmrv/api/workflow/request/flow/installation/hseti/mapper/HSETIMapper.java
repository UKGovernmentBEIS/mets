package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETICompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecisionOperatorAmendsNeededDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIReviewGroup;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface HSETIMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "hsetiAttachments", ignore = true)
    HSETIApplicationSubmittedRequestActionPayload toHSETIApplicationSubmittedRequestActionPayload(HSETIApplicationSubmitRequestTaskPayload taskPayload,
                                                                                                  RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", source = "requestTaskPayloadType")
    HSETIApplicationRegulatorReviewSubmitRequestTaskPayload toHSETIApplicationRegulatorReviewSubmitRequestTaskPayload(
            HSETIRequestPayload requestPayload,
            RequestTaskPayloadType requestTaskPayloadType);


    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    HSETIRegulatorReviewReturnedForAmendsRequestActionPayload toHSETIRegulatorReviewReturnedForAmendsRequestActionPayload(
        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload payload,
        RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestTaskPayloadType.HSE_TI_APPLICATION_AMENDS_SUBMIT_PAYLOAD)")
    @Mapping(target = "regulatorReviewGroupDecisions", source = "requestPayload.regulatorReviewGroupDecisions", qualifiedByName = "fixOperatorAmendsPayloadRegulatorGroupDecisions")
    @Mapping(target = "attachments", ignore = true)
    HSETIApplicationAmendsSubmitRequestTaskPayload toHSETIApplicationAmendsSubmitRequestTaskPayload(HSETIRequestPayload requestPayload);

        @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestActionPayloadType.HSE_TI_COMPLETED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
        HSETICompletedRequestActionPayload toHSETICompletedRequestActionPayload(HSETIRequestPayload requestPayload);

    @Named("fixOperatorAmendsPayloadRegulatorGroupDecisions")
    default Map<HSETIReviewGroup, HSETIRegulatorReviewDecision> fixOperatorAmendsPayloadRegulatorGroupDecisions(Map<HSETIReviewGroup, HSETIRegulatorReviewDecision> regulatorReviewGroupDecisions) {

        return regulatorReviewGroupDecisions.entrySet()
            .stream()
            .map(entry -> {
                HSETIRegulatorReviewDecision reviewDecision = entry.getValue();
                return new AbstractMap.SimpleEntry<>(entry.getKey(),
                    HSETIRegulatorReviewDecision.builder()
                        .type(reviewDecision.getType())
                        .details(HSETIRegulatorReviewDecisionOperatorAmendsNeededDetails
                                .builder()
                                .requiredChanges(((HSETIRegulatorReviewDecisionOperatorAmendsNeededDetails) reviewDecision.getDetails()).getRequiredChanges())
                                .build())
                        .build());
                }
            )
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }
}
