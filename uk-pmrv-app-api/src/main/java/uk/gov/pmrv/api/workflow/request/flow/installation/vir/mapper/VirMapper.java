package uk.gov.pmrv.api.workflow.request.flow.installation.vir.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

import java.time.Year;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface VirMapper {

    default VirVerificationData toVirVerificationData(AerVerificationData aerVerificationData, InstallationCategory installationCategory) {
        VirVerificationData verificationData = new VirVerificationData();

        Optional.ofNullable(aerVerificationData.getUncorrectedNonConformities()).ifPresent(uncorrectedNonConformities -> {
            uncorrectedNonConformities.getUncorrectedNonConformities()
                    .forEach(item -> verificationData.getUncorrectedNonConformities().put(item.getReference(), item));
            uncorrectedNonConformities.getPriorYearIssues()
                    .forEach(item -> verificationData.getPriorYearIssues().put(item.getReference(), item));
        });

        List<InstallationCategory> applicableCategoriesForRecommendedImprovements = List.of(InstallationCategory.A,
                InstallationCategory.B, InstallationCategory.C);

        if(!ObjectUtils.isEmpty(aerVerificationData.getRecommendedImprovements())
                && !ObjectUtils.isEmpty(aerVerificationData.getRecommendedImprovements().getRecommendedImprovements())
                && applicableCategoriesForRecommendedImprovements.contains(installationCategory)) {
            aerVerificationData.getRecommendedImprovements().getRecommendedImprovements()
                    .forEach(item -> verificationData.getRecommendedImprovements().put(item.getReference(), item));
        }

        return verificationData;
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.VIR_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "virAttachments", ignore = true)
    VirApplicationSubmittedRequestActionPayload toVirApplicationSubmittedRequestActionPayload(VirApplicationSubmitRequestTaskPayload taskPayload,
                                                                                              Year reportingYear);

    @AfterMapping
    default void setVirAttachments(@MappingTarget VirApplicationSubmittedRequestActionPayload actionPayload, VirApplicationSubmitRequestTaskPayload taskPayload) {
        actionPayload.setVirAttachments(taskPayload.getVirAttachments());
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.VIR_APPLICATION_REVIEWED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    VirApplicationReviewedRequestActionPayload toVirApplicationReviewedRequestActionPayload(VirRequestPayload requestPayload,
                                                                                            Year reportingYear);
    
    @Mapping(target = "regulatorReviewResponse.regulatorImprovementResponses", source = "regulatorReviewResponse.regulatorImprovementResponses", qualifiedByName = "regulatorImprovementResponsesIgnoreComments")
    VirApplicationReviewedRequestActionPayload cloneReviewedPayloadIgnoreComments(VirApplicationReviewedRequestActionPayload payload);

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

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    VirApplicationRespondedToRegulatorCommentsRequestActionPayload toVirApplicationRespondedToRegulatorCommentsRequestActionPayload(VirRequestPayload requestPayload,
                                                                                                                                    Year reportingYear,
                                                                                                                                    OperatorImprovementFollowUpResponse operatorImprovementFollowUpResponse,
                                                                                                                                    String reference);

    @AfterMapping
    default void setImprovementResponses(@MappingTarget VirApplicationRespondedToRegulatorCommentsRequestActionPayload actionPayload, VirRequestPayload requestPayload, String reference) {
        actionPayload.setOperatorImprovementResponse(requestPayload.getOperatorImprovementResponses().get(reference));
        actionPayload.setRegulatorImprovementResponse(requestPayload.getRegulatorReviewResponse().getRegulatorImprovementResponses().get(reference));

        Map<String, UncorrectedItem> uncorrectedItemMap = Stream.of(
                requestPayload.getVerificationData().getUncorrectedNonConformities())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

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
    VirApplicationRespondedToRegulatorCommentsRequestActionPayload cloneRespondedPayloadIgnoreComments(VirApplicationRespondedToRegulatorCommentsRequestActionPayload payload);
}
