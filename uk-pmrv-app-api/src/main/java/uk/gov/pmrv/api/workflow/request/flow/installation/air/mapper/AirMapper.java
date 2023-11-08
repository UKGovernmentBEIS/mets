package uk.gov.pmrv.api.workflow.request.flow.installation.air.mapper;

import java.time.Year;
import java.util.AbstractMap;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AirMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.AIR_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "airAttachments", ignore = true)
    AirApplicationSubmittedRequestActionPayload toAirApplicationSubmittedRequestActionPayload(
        AirApplicationSubmitRequestTaskPayload taskPayload,
        Year reportingYear
    );

    @AfterMapping
    default void setAirAttachments(@MappingTarget AirApplicationSubmittedRequestActionPayload actionPayload,
                                   AirApplicationSubmitRequestTaskPayload taskPayload) {

        actionPayload.setAirAttachments(taskPayload.getAirAttachments());
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.AIR_APPLICATION_REVIEWED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    AirApplicationReviewedRequestActionPayload toAirApplicationReviewedRequestActionPayload(
        AirRequestPayload requestPayload,
        Year reportingYear);

    @Mapping(target = "regulatorReviewResponse.regulatorImprovementResponses", source = "regulatorReviewResponse.regulatorImprovementResponses", qualifiedByName = "regulatorImprovementResponsesIgnoreComments")
    AirApplicationReviewedRequestActionPayload cloneReviewedPayloadIgnoreComments(AirApplicationReviewedRequestActionPayload payload);
    
    @Named("regulatorImprovementResponsesIgnoreComments")
    default Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponsesIgnoreComments(
        Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses) {
        
        return regulatorImprovementResponses.entrySet().stream().map(entry ->
                new AbstractMap.SimpleEntry<>(entry.getKey(),
                    RegulatorAirImprovementResponse.builder()
                        .improvementRequired(entry.getValue().getImprovementRequired())
                        .improvementDeadline(entry.getValue().getImprovementDeadline())
                        .officialResponse(entry.getValue().getOfficialResponse())
                        .files(entry.getValue().getFiles())
                        .build())
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "airAttachments", ignore = true)
    @Mapping(target = "reviewAttachments", ignore = true)
    AirApplicationRespondedToRegulatorCommentsRequestActionPayload toAirApplicationRespondedToRegulatorCommentsRequestActionPayload(
        AirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload,
        Year reportingYear,
        OperatorAirImprovementFollowUpResponse operatorImprovementFollowUpResponse,
        Integer reference);

    @AfterMapping
    default void setImprovementResponses(@MappingTarget
                                         AirApplicationRespondedToRegulatorCommentsRequestActionPayload actionPayload,
                                         AirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload,
                                         Integer reference) {

        actionPayload.setAirImprovement(taskPayload.getAirImprovements().get(reference));
        actionPayload.setOperatorImprovementResponse(taskPayload.getOperatorImprovementResponses().get(reference));
        actionPayload.setRegulatorImprovementResponse(taskPayload.getRegulatorImprovementResponses().get(reference));

        final Set<UUID> actionFiles = Stream.of(
                actionPayload.getOperatorImprovementResponse().getAirFiles(),
                actionPayload.getRegulatorImprovementResponse().getFiles(),
                actionPayload.getOperatorImprovementFollowUpResponse().getFiles()
            ).flatMap(Set::stream)
            .collect(Collectors.toSet());

        actionPayload.setAirAttachments(
            taskPayload.getAirAttachments().entrySet().stream()
                .filter(e -> actionFiles.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );

        actionPayload.setReviewAttachments(
            taskPayload.getReviewAttachments().entrySet().stream()
                .filter(e -> actionFiles.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
    
    @Mapping(target = "regulatorImprovementResponse.comments", ignore = true)
    AirApplicationRespondedToRegulatorCommentsRequestActionPayload cloneRespondedPayloadIgnoreComments(AirApplicationRespondedToRegulatorCommentsRequestActionPayload payload);
}
