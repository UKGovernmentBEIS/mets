package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERNerDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationCompletedRequestActionPayload;

import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewGroup;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface NERMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "nerAttachments", ignore = true)
    NERApplicationSubmittedRequestActionPayload toNERApplicationSubmittedRequestActionPayload(
            NerApplicationSubmitRequestTaskPayload taskPayload,
            RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestTaskPayloadType.NER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "nerSectionsCompleted", ignore = true)
    NERApplicationVerificationSubmitRequestTaskPayload toNERApplicationVerificationRequestTaskPayload(
            NerRequestPayload requestPayload,
            InstallationOperatorDetails installationOperatorDetails,
            NERVerificationReport verificationReport);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestActionPayloadType.NER_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    NERApplicationVerificationSubmittedRequestActionPayload toNERApplicationVerificationSubmittedRequestActionPayload(NERApplicationVerificationSubmitRequestTaskPayload taskPayload);

    @Mapping(target = "payloadType", source = "requestTaskPayloadType")
    @Mapping(target = "verificationReport", source = "requestPayload.verificationReport")
    NERApplicationRegulatorReviewSubmitRequestTaskPayload toNERApplicationRegulatorReviewSubmitRequestTaskPayload(
            NerRequestPayload requestPayload,
            RequestTaskPayloadType requestTaskPayloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "regulatorReviewGroupDecisions", source = "payload.regulatorReviewGroupDecisions")
    @Mapping(target = "attachments", ignore = true)
    NERRegulatorReviewReturnedForAmendsRequestActionPayload toNERRegulatorReviewReturnedForAmendsRequestActionPayload(
            NERApplicationRegulatorReviewSubmitRequestTaskPayload payload,
            RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestTaskPayloadType.NER_APPLICATION_AMENDS_SUBMIT_PAYLOAD)")
    @Mapping(target = "regulatorReviewGroupDecisions", source = "requestPayload.regulatorReviewGroupDecisions", qualifiedByName =
            "reviewGroupDecisionsForOperatorAmend")
    @Mapping(target = "verificationBodyId", source = "requestPayload", qualifiedByName = "verificationBodyId")
    NERApplicationAmendsSubmitRequestTaskPayload toNERApplicationAmendsSubmitRequestTaskPayload(
            NerRequestPayload requestPayload, NERRequestMetadata metadata);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "attachments", ignore = true)
    NERApplicationCompletedRequestActionPayload toNERApplicationCompletedRequestActionPayload(
            NerRequestPayload requestPayload,
            InstallationOperatorDetails installationOperatorDetails,
            NERVerificationReport verificationReport,
            RequestActionPayloadType payloadType);

    @Named("reviewGroupDecisionsForOperatorAmend")
    default Map<NERReviewGroup, NERReviewDecision> setReviewGroupDecisionsForOperatorAmend(
            Map<NERReviewGroup, NERReviewDecision> regulatorReviewGroupDecisions) {
        return regulatorReviewGroupDecisions.entrySet()
                .stream()
                .filter(entry ->entry.getKey().equals(NERReviewGroup.NER))
                .map(entry -> {
                            NERNerDataRegulatorReviewDecision nerDataReviewDecision =
                                    (NERNerDataRegulatorReviewDecision) entry.getValue();
                            return new AbstractMap.SimpleEntry<>(entry.getKey(),
                                    NERNerDataRegulatorReviewDecision.builder()
                                            .type(nerDataReviewDecision.getType())
                                            .reviewDataType(NERReviewDataType.NER_DATA)
                                            .details(NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                                    .verificationRequired(((NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails)
                                                            nerDataReviewDecision.getDetails()).getVerificationRequired())
                                                    .requiredChanges(((NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails)
                                                            nerDataReviewDecision.getDetails()).getRequiredChanges()).build())
                                            .build());
                        }
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Named("verificationBodyId")
    default Long setVerificationBodyId(NerRequestPayload requestPayload) {
        return requestPayload.isVerificationPerformed() ?
                requestPayload.getVerificationReport().getVerificationBodyId() : null;
    }
}
