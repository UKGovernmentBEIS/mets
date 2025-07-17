package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerUkEtsSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
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
public interface AviationAerUkEtsReviewMapper {

    AviationAerUkEtsContainer toAviationAerUkEtsContainer(AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload taskPayload, EmissionTradingScheme scheme);

    AviationAerUkEtsContainer toAviationAerUkEtsContainer(AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload taskPayload,
                                                          AviationAerUkEtsVerificationReport verificationReport,
                                                          EmissionTradingScheme scheme);
    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "verificationReport", source = "requestPayload.verificationReport")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerUkEtsApplicationReviewRequestTaskPayload toAviationAerUkEtsApplicationReviewRequestTaskPayload(
        AviationAerUkEtsRequestPayload requestPayload,
        RequestAviationAccountInfo accountInfo,
        RequestTaskPayloadType payloadType,
        Year reportingYear);

    @AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload,
                                            RequestAviationAccountInfo accountInfo) {
        AviationAerUkEts aer = reviewRequestTaskPayload.getAer();
        //if statement has been added for the case where the operator has no reporting obligation, thus aer will be null
        if(aer != null) {
            AviationOperatorDetails operatorDetails = aer.getOperatorDetails();
            operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
        }
    }

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewGroupDecisions", source = "requestTaskPayload.reviewGroupDecisions", qualifiedByName = "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "attachments", ignore = true)
    AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload toAviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload(
        AviationAerUkEtsApplicationReviewRequestTaskPayload requestTaskPayload,
        RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewGroupDecisions", source = "requestPayload.reviewGroupDecisions", qualifiedByName =
        "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "verificationBodyId", source = "requestPayload", qualifiedByName = "verificationBodyId")
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload toAviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload(
        AviationAerUkEtsRequestPayload requestPayload,
        RequestTaskPayloadType payloadType,
        Year reportingYear);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "aerAttachments", ignore = true)
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerUkEtsApplicationSubmittedRequestActionPayload toAviationAerUkEtsApplicationSubmittedRequestActionPayload(
        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload taskPayload,
        RequestAviationAccountInfo accountInfo,
        AviationAerUkEtsSubmittedEmissions submittedEmissions,
        RequestActionPayloadType payloadType);

    @AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget AviationAerUkEtsApplicationSubmittedRequestActionPayload requestActionPayload,
                                            RequestAviationAccountInfo accountInfo) {
        AviationAerUkEts aer = requestActionPayload.getAer();
        if(aer != null) {
            AviationOperatorDetails operatorDetails = aer.getOperatorDetails();
            operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
        }
    }

    @AfterMapping
    default void setAerAttachments(@MappingTarget AviationAerUkEtsApplicationSubmittedRequestActionPayload requestActionPayload,
                                   AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload taskPayload) {
        requestActionPayload.setAerAttachments(taskPayload.getAerAttachments());
    }

    @Named("verificationBodyId")
    default Long setVerificationBodyId(AviationAerUkEtsRequestPayload requestPayload) {
        return requestPayload.isVerificationPerformed() ?
            requestPayload.getVerificationReport().getVerificationBodyId() : null;
    }

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<AviationAerUkEtsReviewGroup, AerReviewDecision> setReviewGroupDecisionsForOperatorAmend(Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecision) {
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
}
