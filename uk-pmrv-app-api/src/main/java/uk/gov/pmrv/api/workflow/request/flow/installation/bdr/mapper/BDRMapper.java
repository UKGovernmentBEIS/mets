package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRBdrDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationReport;


import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BDRMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "bdrAttachments", ignore = true)
    BDRApplicationSubmittedRequestActionPayload toBDRApplicationSubmittedRequestActionPayload(BDRApplicationSubmitRequestTaskPayload taskPayload,
                                                                                              RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestActionPayloadType.BDR_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    BDRApplicationVerificationSubmittedRequestActionPayload toBDRApplicationVerificationSubmittedRequestActionPayload(BDRApplicationVerificationSubmitRequestTaskPayload taskPayload);


    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestTaskPayloadType.BDR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "bdrSectionsCompleted", ignore = true)
    BDRApplicationVerificationSubmitRequestTaskPayload toBDRApplicationVerificationRequestTaskPayload(
        BDRRequestPayload requestPayload,
        InstallationOperatorDetails installationOperatorDetails,
        BDRVerificationReport verificationReport);

    @Mapping(target = "payloadType", source = "requestTaskPayloadType")
    @Mapping(target = "verificationReport", source = "requestPayload.verificationReport")
    BDRApplicationRegulatorReviewSubmitRequestTaskPayload toBDRApplicationRegulatorReviewSubmitRequestTaskPayload(
        BDRRequestPayload requestPayload,
        RequestTaskPayloadType requestTaskPayloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "regulatorReviewGroupDecisions", source = "payload.regulatorReviewGroupDecisions", qualifiedByName =
        "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "attachments", ignore = true)
    BDRRegulatorReviewReturnedForAmendsRequestActionPayload toBDRRegulatorReviewReturnedForAmendsRequestActionPayload(
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload payload,
        RequestActionPayloadType payloadType);


    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestTaskPayloadType.BDR_APPLICATION_AMENDS_SUBMIT_PAYLOAD)")
    @Mapping(target = "regulatorReviewGroupDecisions", source = "requestPayload.regulatorReviewGroupDecisions", qualifiedByName =
        "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "verificationBodyId", source = "requestPayload", qualifiedByName = "verificationBodyId")
    BDRApplicationAmendsSubmitRequestTaskPayload toBDRApplicationAmendsSubmitRequestTaskPayload(
                BDRRequestPayload requestPayload, BDRRequestMetadata metadata);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestActionPayloadType.BDR_APPLICATION_COMPLETED_PAYLOAD)")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "attachments", ignore = true)
    BDRApplicationCompletedRequestActionPayload toBDRApplicationCompletedRequestActionPayload(
        BDRRequestPayload requestPayload,
        InstallationOperatorDetails installationOperatorDetails,
        BDRVerificationReport verificationReport);

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<BDRReviewGroup, BDRReviewDecision> setReviewGroupDecisionsForOperatorAmend(
        Map<BDRReviewGroup, BDRReviewDecision> regulatorReviewGroupDecisions) {
        return regulatorReviewGroupDecisions.entrySet()
                                .stream()
                                .filter(entry ->entry.getKey().equals(BDRReviewGroup.BDR))
                                .map(entry -> {
                                    BDRBdrDataRegulatorReviewDecision bdrDataReviewDecision =
                                            (BDRBdrDataRegulatorReviewDecision) entry.getValue();
                                    return new AbstractMap.SimpleEntry<>(entry.getKey(),
                                        BDRBdrDataRegulatorReviewDecision.builder()
                                            .type(bdrDataReviewDecision.getType())
                                            .reviewDataType(BDRReviewDataType.BDR_DATA)
                                            .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                                .verificationRequired(((BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails)
                                                    bdrDataReviewDecision.getDetails()).getVerificationRequired())
                                                .requiredChanges(((BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails)
                                                        bdrDataReviewDecision.getDetails()).getRequiredChanges()).build())
                                            .build());
                                    }
                                )
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    @Named("verificationBodyId")
    default Long setVerificationBodyId(BDRRequestPayload requestPayload) {
        return requestPayload.isVerificationPerformed() ?
            requestPayload.getVerificationReport().getVerificationBodyId() : null;
    }

}
