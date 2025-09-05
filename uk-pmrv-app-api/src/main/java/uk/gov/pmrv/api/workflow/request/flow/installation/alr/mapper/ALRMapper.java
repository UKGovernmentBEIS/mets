package uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAlrDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAcceptedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAcceptedWithCorrectionsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAuthorityResponseSubmittedRequestActionPayload;


import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;



@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface ALRMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "alrAttachments", ignore = true)
    ALRApplicationSubmittedRequestActionPayload toALRApplicationSubmittedRequestActionPayload(ALRApplicationSubmitRequestTaskPayload taskPayload,
                                                                                              RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestTaskPayloadType.ALR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "alrSectionsCompleted", ignore = true)
    ALRApplicationVerificationSubmitRequestTaskPayload toALRApplicationVerificationRequestTaskPayload(
            ALRRequestPayload requestPayload,
            InstallationOperatorDetails installationOperatorDetails,
            ALRVerificationReport verificationReport);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestActionPayloadType.ALR_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    ALRApplicationVerificationSubmittedRequestActionPayload toALRApplicationVerificationSubmittedRequestActionPayload(ALRApplicationVerificationSubmitRequestTaskPayload taskPayload);

    @Mapping(target = "payloadType", source = "requestTaskPayloadType")
    @Mapping(target = "verificationReport", source = "requestPayload.verificationReport")
    ALRApplicationRegulatorReviewSubmitRequestTaskPayload toALRApplicationRegulatorReviewSubmitRequestTaskPayload(
            ALRRequestPayload requestPayload,
            RequestTaskPayloadType requestTaskPayloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestTaskPayloadType.ALR_APPLICATION_AMENDS_SUBMIT_PAYLOAD)")
    @Mapping(target = "regulatorReviewGroupDecisions", source = "requestPayload.regulatorReviewGroupDecisions", qualifiedByName =
            "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "verificationBodyId", source = "requestPayload", qualifiedByName = "verificationBodyId")
    ALRApplicationAmendsSubmitRequestTaskPayload toALRApplicationAmendsSubmitRequestTaskPayload(
            ALRRequestPayload requestPayload, ALRRequestMetaData metadata);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "regulatorReviewGroupDecisions", source = "payload.regulatorReviewGroupDecisions", qualifiedByName =
            "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "attachments", ignore = true)
    ALRRegulatorReviewReturnedForAmendsRequestActionPayload toALRRegulatorReviewReturnedForAmendsRequestActionPayload(
            ALRApplicationRegulatorReviewSubmitRequestTaskPayload payload,
            RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.ALR_APPLICATION_PROCEEDED_TO_AUTHORITY_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    ALRApplicationProceededToAuthorityRequestActionPayload toALRApplicationProceededToAuthorityRequestActionPayload(ALRRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.ALR_APPLICATION_CLOSED_PAYLOAD)")
    ALRApplicationClosedRequestActionPayload toALRApplicationClosedRequestActionPayload(ALRRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.ALR_APPLICATION_ACCEPTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    ALRApplicationAcceptedRequestActionPayload toALRApplicationAcceptedRequestActionPayload(ALRRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.ALR_APPLICATION_ACCEPTED_WITH_CORRECTIONS_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    ALRApplicationAcceptedWithCorrectionsRequestActionPayload toALRApplicationAcceptedWithCorrectionsRequestActionPayload(ALRRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.ALR_APPLICATION_REJECTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    ALRApplicationRejectedRequestActionPayload toALRApplicationRejectedRequestActionPayload(ALRRequestPayload requestPayload);

    default ALRAuthorityResponseSubmittedRequestActionPayload toALRAuthorityResponseSubmittedRequestActionPayload(ALRRequestPayload requestPayload, RequestActionType actionType) {
        return switch (actionType) {
            case ALR_APPLICATION_ACCEPTED -> toALRApplicationAcceptedRequestActionPayload(requestPayload);
            case ALR_APPLICATION_ACCEPTED_WITH_CORRECTIONS -> toALRApplicationAcceptedWithCorrectionsRequestActionPayload(requestPayload);
            case ALR_APPLICATION_REJECTED -> toALRApplicationRejectedRequestActionPayload(requestPayload);
            default -> null;
        };
    }

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<ALRReviewGroup, ALRReviewDecision> setReviewGroupDecisionsForOperatorAmend(
            Map<ALRReviewGroup, ALRReviewDecision> regulatorReviewGroupDecisions) {
        return regulatorReviewGroupDecisions.entrySet()
                .stream()
                .filter(entry ->entry.getKey().equals(ALRReviewGroup.ALR))
                .map(entry -> {
                            ALRAlrDataRegulatorReviewDecision alrDataReviewDecision =
                                    (ALRAlrDataRegulatorReviewDecision) entry.getValue();
                            return new AbstractMap.SimpleEntry<>(entry.getKey(),
                                    ALRAlrDataRegulatorReviewDecision.builder()
                                            .type(alrDataReviewDecision.getType())
                                            .reviewDataType(ALRReviewDataType.ALR_DATA)
                                            .details(ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                                    .verificationRequired(((ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails)
                                                            alrDataReviewDecision.getDetails()).getVerificationRequired())
                                                    .requiredChanges(((ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails)
                                                            alrDataReviewDecision.getDetails()).getRequiredChanges()).build())
                                            .build());
                        }
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Named("verificationBodyId")
    default Long setVerificationBodyId(ALRRequestPayload requestPayload) {
        return requestPayload.isVerificationPerformed() ?
                requestPayload.getVerificationReport().getVerificationBodyId() : null;
    }
}
