package uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivity;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;

import java.time.Year;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AerMapper {

    AerRegulatedActivity toAerRegulatedActivity(RegulatedActivity regulatedActivity);

    AerContainer toAerContainer(AerApplicationSubmitRequestTaskPayload taskPayload);

    AerContainer toAerContainer(AerApplicationSubmitRequestTaskPayload taskPayload,
                                AerVerificationReport verificationReport);

    AerContainer toAerContainer(AerApplicationAmendsSubmitRequestTaskPayload taskPayload);

    @Mapping(target = "reportingYear", source = "metadata.year")
    @Mapping(target = "reportableEmissions", ignore = true)
    AerContainer toAerContainer(AerRequestPayload requestPayload,
                                InstallationOperatorDetails installationOperatorDetails,
                                AerRequestMetadata metadata);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestActionPayloadType.AER_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "aerAttachments", ignore = true)
    AerApplicationSubmittedRequestActionPayload toAerApplicationSubmittedRequestActionPayload(
        AerApplicationSubmitRequestTaskPayload taskPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestActionPayloadType.AER_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "aerAttachments", ignore = true)
    AerApplicationAmendsSubmittedRequestActionPayload toAerApplicationAmendsSubmittedRequestActionPayload(
        AerApplicationSubmitRequestTaskPayload taskPayload);

    @AfterMapping
    default void setAerAttachments(@MappingTarget AerApplicationSubmittedRequestActionPayload actionPayload,
                                   AerApplicationSubmitRequestTaskPayload taskPayload) {
        actionPayload.setAerAttachments(taskPayload.getAerAttachments());
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestTaskPayloadType.AER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "aerSectionsCompleted", ignore = true)
    AerApplicationVerificationSubmitRequestTaskPayload toAerApplicationVerificationRequestTaskPayload(
        AerRequestPayload requestPayload,
        InstallationOperatorDetails installationOperatorDetails,
        AerVerificationReport verificationReport,
        Year reportingYear);

    @Mapping(target = "payloadType", source = "requestTaskPayloadType")
    @Mapping(target = "verificationReport", source = "requestPayload.verificationReport")
    AerApplicationReviewRequestTaskPayload toAerApplicationReviewRequestTaskPayload(
        AerRequestPayload requestPayload,
        InstallationOperatorDetails installationOperatorDetails,
        RequestTaskPayloadType requestTaskPayloadType,
        Year reportingYear);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestActionPayloadType.AER_APPLICATION_COMPLETED_PAYLOAD)")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "attachments", ignore = true)
    AerApplicationCompletedRequestActionPayload toAerApplicationCompletedRequestActionPayload(
        AerRequestPayload requestPayload,
        InstallationOperatorDetails installationOperatorDetails,
        AerVerificationReport verificationReport,
        Year reportingYear);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestActionPayloadType.AER_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    AerApplicationVerificationSubmittedRequestActionPayload toAerApplicationVerificationSubmittedRequestActionPayload(AerApplicationVerificationSubmitRequestTaskPayload taskPayload);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewGroupDecisions", source = "payload.reviewGroupDecisions", qualifiedByName =
        "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "reviewAttachments", ignore = true)
    AerApplicationReturnedForAmendsRequestActionPayload toAerApplicationReturnedForAmendsRequestActionPayload(
        AerApplicationReviewRequestTaskPayload payload,
        RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestTaskPayloadType.AER_APPLICATION_AMENDS_SUBMIT_PAYLOAD)")
    @Mapping(target = "reviewGroupDecisions", source = "aerRequestPayload.reviewGroupDecisions", qualifiedByName =
        "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "verificationBodyId", source = "aerRequestPayload", qualifiedByName = "verificationBodyId")
    @Mapping(target = "reportingYear", source = "metadata.year")
    @Mapping(target = "permitType", source = "aerRequestPayload.permitOriginatedData.permitType")
    AerApplicationAmendsSubmitRequestTaskPayload toAerApplicationAmendsSubmitRequestTaskPayload(
        AerRequestPayload aerRequestPayload, AerRequestMetadata metadata);

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<AerReviewGroup, AerReviewDecision> setReviewGroupDecisionsForOperatorAmend(
        Map<AerReviewGroup, AerReviewDecision> reviewGroupDecision) {
        return reviewGroupDecision.entrySet().stream()
            .filter(aerReviewDecision -> aerReviewDecision.getValue().getReviewDataType() == AerReviewDataType.AER_DATA)
            .filter(entry -> ((AerDataReviewDecision) entry.getValue()).getType() == AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .map(entry -> {
                    AerDataReviewDecision aerDataReviewDecision = (AerDataReviewDecision) entry.getValue();
                    return new AbstractMap.SimpleEntry<>(entry.getKey(),
                        AerDataReviewDecision.builder()
                            .type(aerDataReviewDecision.getType())
                            .reviewDataType(AerReviewDataType.AER_DATA)
                            .details(ChangesRequiredDecisionDetails.builder()
                                .requiredChanges(((ChangesRequiredDecisionDetails) aerDataReviewDecision.getDetails()).getRequiredChanges()).build())
                            .build());
                }
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Named("verificationBodyId")
    default Long setVerificationBodyId(AerRequestPayload aerRequestPayload) {
        return aerRequestPayload.isVerificationPerformed() ?
            aerRequestPayload.getVerificationReport().getVerificationBodyId() : null;
    }
}
