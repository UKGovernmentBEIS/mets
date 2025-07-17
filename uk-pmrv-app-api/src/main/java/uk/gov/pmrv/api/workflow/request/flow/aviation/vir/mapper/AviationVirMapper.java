package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.mapper;

import java.time.Year;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationVirMapper {

    VirVerificationData toVirVerificationData(final AviationAerVerificationData aerVerificationData);


    @AfterMapping
    default void toVirVerificationData(@MappingTarget VirVerificationData verificationData,
                                       AviationAerVerificationData aerVerificationData) {
            
        if (aerVerificationData instanceof AviationAerUkEtsVerificationData aerUkEtsVerificationData) {
            Optional.ofNullable(aerUkEtsVerificationData.getUncorrectedNonConformities())
                .ifPresent(uncorrectedNonConformities -> {
                    uncorrectedNonConformities.getUncorrectedNonConformities()
                        .forEach(
                            item -> verificationData.getUncorrectedNonConformities().put(item.getReference(), item));
                    uncorrectedNonConformities.getPriorYearIssues()
                        .forEach(item -> verificationData.getPriorYearIssues().put(item.getReference(), item));
                });

            if (!ObjectUtils.isEmpty(aerUkEtsVerificationData.getRecommendedImprovements()) &&
                !ObjectUtils.isEmpty(aerUkEtsVerificationData.getRecommendedImprovements().getRecommendedImprovements())) {

                aerUkEtsVerificationData.getRecommendedImprovements().getRecommendedImprovements()
                    .forEach(item -> verificationData.getRecommendedImprovements().put(item.getReference(), item));
            }
        }
        
        if (aerVerificationData instanceof AviationAerCorsiaVerificationData aerCorsiaVerificationData) {
            Optional.ofNullable(aerCorsiaVerificationData.getUncorrectedNonConformities())
                .ifPresent(uncorrectedNonConformities -> uncorrectedNonConformities.getUncorrectedNonConformities()
                    .forEach(
                        item -> verificationData.getUncorrectedNonConformities().put(item.getReference(), item)));

            if (!ObjectUtils.isEmpty(aerCorsiaVerificationData.getRecommendedImprovements()) &&
                !ObjectUtils.isEmpty(aerCorsiaVerificationData.getRecommendedImprovements().getRecommendedImprovements())) {

                aerCorsiaVerificationData.getRecommendedImprovements().getRecommendedImprovements()
                    .forEach(item -> verificationData.getRecommendedImprovements().put(item.getReference(), item));
            }
        }
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.AVIATION_VIR_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "virAttachments", ignore = true)
    AviationVirApplicationSubmittedRequestActionPayload toVirApplicationSubmittedRequestActionPayload(
        AviationVirApplicationSubmitRequestTaskPayload taskPayload,
        Year reportingYear);

    @AfterMapping
    default void setVirAttachments(@MappingTarget AviationVirApplicationSubmittedRequestActionPayload actionPayload,
                                   AviationVirApplicationSubmitRequestTaskPayload taskPayload) {
        actionPayload.setVirAttachments(taskPayload.getVirAttachments());
    }
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.AVIATION_VIR_APPLICATION_REVIEWED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    AviationVirApplicationReviewedRequestActionPayload toVirApplicationReviewedRequestActionPayload(
        AviationVirRequestPayload requestPayload,
        Year reportingYear);

    @Mapping(target = "regulatorReviewResponse.regulatorImprovementResponses", source = "regulatorReviewResponse.regulatorImprovementResponses", qualifiedByName = "regulatorImprovementResponsesIgnoreComments")
    AviationVirApplicationReviewedRequestActionPayload cloneReviewedPayloadIgnoreComments(
        AviationVirApplicationReviewedRequestActionPayload payload);

    @Named("regulatorImprovementResponsesIgnoreComments")
    default Map<String, RegulatorImprovementResponse> regulatorImprovementResponsesIgnoreComments(
        Map<String, RegulatorImprovementResponse> regulatorImprovementResponses) {

        return regulatorImprovementResponses.entrySet().stream().map(entry ->
            new AbstractMap.SimpleEntry<>(entry.getKey(),
                RegulatorImprovementResponse.builder()
                    .improvementRequired(entry.getValue().isImprovementRequired())
                    .improvementDeadline(entry.getValue().getImprovementDeadline())
                    .operatorActions(entry.getValue().getOperatorActions())
                    .build())
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload toVirApplicationRespondedToRegulatorCommentsRequestActionPayload(
        AviationVirRequestPayload requestPayload,
        Year reportingYear,
        OperatorImprovementFollowUpResponse operatorImprovementFollowUpResponse,
        String reference);

    @AfterMapping
    default void setImprovementResponses(
        @MappingTarget AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload actionPayload,
        AviationVirRequestPayload requestPayload, 
        String reference
    ) {
        actionPayload.setOperatorImprovementResponse(requestPayload.getOperatorImprovementResponses().get(reference));
        actionPayload.setRegulatorImprovementResponse(requestPayload.getRegulatorReviewResponse().getRegulatorImprovementResponses().get(reference));

        Map<String, UncorrectedItem> uncorrectedItemMap = requestPayload.getVerificationData().getUncorrectedNonConformities();

        Optional.ofNullable(uncorrectedItemMap.get(reference)).ifPresentOrElse(
            actionPayload::setVerifierUncorrectedItem,
            () -> {
                Map<String, VerifierComment> verifierCommentItemMap = Stream.of(
                        requestPayload.getVerificationData().getPriorYearIssues(),
                        requestPayload.getVerificationData().getRecommendedImprovements())
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Optional.ofNullable(verifierCommentItemMap.get(reference)).ifPresent(actionPayload::setVerifierComment);
            });
    }

    @Mapping(target = "regulatorImprovementResponse.improvementComments", ignore = true)
    AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload cloneRespondedPayloadIgnoreComments(
        AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload payload
    );
}
