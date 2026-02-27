package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationCompletedRequestActionPayload;


import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BDRS2Mapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "bdrs2Attachments", ignore = true)
    BDRS2ApplicationSubmittedRequestActionPayload toBDRS2ApplicationSubmittedRequestActionPayload(
            BDRS2ApplicationSubmitRequestTaskPayload taskPayload,
            RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestTaskPayloadType.BDRS2_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "bdrs2SectionsCompleted", ignore = true)
    BDRS2ApplicationVerificationSubmitRequestTaskPayload toBDRS2ApplicationVerificationRequestTaskPayload(
            BDRS2RequestPayload requestPayload,
            InstallationOperatorDetails installationOperatorDetails,
            BDRS2VerificationReport verificationReport);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestActionPayloadType.BDRS2_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    BDRS2ApplicationVerificationSubmittedRequestActionPayload toBDRS2ApplicationVerificationSubmittedRequestActionPayload(BDRS2ApplicationVerificationSubmitRequestTaskPayload taskPayload);

    @Mapping(target = "payloadType", source = "requestTaskPayloadType")
    @Mapping(target = "verificationReport", source = "requestPayload.verificationReport")
    BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload toBDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload(
        BDRS2RequestPayload requestPayload,
        RequestTaskPayloadType requestTaskPayloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "regulatorReviewGroupDecisions", source = "payload.regulatorReviewGroupDecisions")
    @Mapping(target = "attachments", ignore = true)
    BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload toBDRS2RegulatorReviewReturnedForAmendsRequestActionPayload(
        BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload payload,
        RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
        ".RequestTaskPayloadType.BDRS2_APPLICATION_AMENDS_SUBMIT_PAYLOAD)")
    @Mapping(target = "regulatorReviewGroupDecisions", source = "requestPayload.regulatorReviewGroupDecisions", qualifiedByName =
        "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "verificationBodyId", source = "requestPayload", qualifiedByName = "verificationBodyId")
    BDRS2ApplicationAmendsSubmitRequestTaskPayload toBDRS2ApplicationAmendsSubmitRequestTaskPayload(
                BDRS2RequestPayload requestPayload, BDRS2RequestMetadata metadata);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestActionPayloadType.BDRS2_APPLICATION_COMPLETED_PAYLOAD)")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "attachments", ignore = true)
    BDRS2ApplicationCompletedRequestActionPayload toBDRS2ApplicationCompletedRequestActionPayload(
            BDRS2RequestPayload requestPayload,
            InstallationOperatorDetails installationOperatorDetails,
            BDRS2VerificationReport verificationReport);

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<BDRS2ReviewGroup, BDRS2ReviewDecision> setReviewGroupDecisionsForOperatorAmend(
        Map<BDRS2ReviewGroup, BDRS2ReviewDecision> regulatorReviewGroupDecisions) {
        return regulatorReviewGroupDecisions.entrySet()
                                .stream()
                                .filter(entry ->entry.getKey().equals(BDRS2ReviewGroup.BDRS2))
                                .map(entry -> {
                                    BDRS2Bdrs2DataRegulatorReviewDecision bdrs2DataReviewDecision =
                                            (BDRS2Bdrs2DataRegulatorReviewDecision) entry.getValue();
                                    return new AbstractMap.SimpleEntry<>(entry.getKey(),
                                        BDRS2Bdrs2DataRegulatorReviewDecision.builder()
                                            .type(bdrs2DataReviewDecision.getType())
                                            .reviewDataType(BDRS2ReviewDataType.BDRS2_DATA)
                                            .details(BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                                .verificationRequired(((BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails)
                                                    bdrs2DataReviewDecision.getDetails()).getVerificationRequired())
                                                .requiredChanges(((BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails)
                                                        bdrs2DataReviewDecision.getDetails()).getRequiredChanges()).build())
                                            .build());
                                    }
                                )
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Named("verificationBodyId")
    default Long setVerificationBodyId(BDRS2RequestPayload requestPayload) {
        return requestPayload.isVerificationPerformed() ?
                requestPayload.getVerificationReport().getVerificationBodyId() : null;
    }

}
