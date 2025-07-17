package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;

import java.time.Year;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAerCorsiaReviewMapper {

    AviationAerCorsiaContainer toAviationAerCorsiaContainer(AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload taskPayload, EmissionTradingScheme scheme);

    AviationAerCorsiaContainer toAviationAerCorsiaContainer(AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload taskPayload,
                                                          AviationAerCorsiaVerificationReport verificationReport,
                                                          EmissionTradingScheme scheme);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "verificationReport", source = "requestPayload.verificationReport")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerCorsiaApplicationReviewRequestTaskPayload toAviationAerCorsiaApplicationReviewRequestTaskPayload(
        AviationAerCorsiaRequestPayload requestPayload,
        RequestAviationAccountInfo accountInfo,
        RequestTaskPayloadType payloadType,
        Year reportingYear);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewGroupDecisions", source = "requestTaskPayload.reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "attachments", ignore = true)
    AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload toAviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload(
            AviationAerCorsiaApplicationReviewRequestTaskPayload requestTaskPayload,
            RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "reviewGroupDecisions", source = "requestPayload.reviewGroupDecisions", qualifiedByName =
            "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "verificationBodyId", source = "requestPayload", qualifiedByName = "verificationBodyId")
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload toAviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload(
            AviationAerCorsiaRequestPayload requestPayload,
            RequestAviationAccountInfo accountInfo,
            RequestTaskPayloadType payloadType,
            Year reportingYear);

    @Named("verificationBodyId")
    default Long setVerificationBodyId(AviationAerCorsiaRequestPayload requestPayload) {
        return requestPayload.isVerificationPerformed() ?
                requestPayload.getVerificationReport().getVerificationBodyId() : null;
    }

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<AviationAerCorsiaReviewGroup, AerReviewDecision> setReviewGroupDecisionsForOperatorAmend(Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecision) {
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

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "aerAttachments", ignore = true)
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerCorsiaApplicationSubmittedRequestActionPayload toAviationAerCorsiaApplicationSubmittedRequestActionPayload(
        AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload taskPayload,
        RequestAviationAccountInfo accountInfo,
        AviationAerCorsiaSubmittedEmissions submittedEmissions,
        RequestActionPayloadType payloadType);

    @AfterMapping
    default void setAerAttachments(@MappingTarget AviationAerCorsiaApplicationSubmittedRequestActionPayload requestActionPayload,
                                   AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload taskPayload) {
        requestActionPayload.setAerAttachments(taskPayload.getAerAttachments());
    }
}
