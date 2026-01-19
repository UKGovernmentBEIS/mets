package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;

import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewAcceptedDecisionDetails;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface WasteQDRMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "wasteQDRAttachments", ignore = true)
    WasteQDRApplicationSubmittedRequestActionPayload toWasteQDRApplicationSubmittedRequestActionPayload(WasteQDRApplicationSubmitRequestTaskPayload taskPayload,
                                                                                                        RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", source = "requestTaskPayloadType")
    WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload toWasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload(
            WasteQDRRequestPayload requestPayload,
            RequestTaskPayloadType requestTaskPayloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestActionPayloadType.WASTE_QDR_APPLICATION_COMPLETED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    WasteQDRApplicationCompletedRequestActionPayload toWasteQDRApplicationCompletedRequestActionPayload(
            WasteQDRRequestPayload requestPayload,
            InstallationOperatorDetails installationOperatorDetails);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "reviewDecision", source = "payload.reviewDecision", qualifiedByName = "reviewDecisionForOperatorAmend")
    WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload toWasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload(
            WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload payload,
            RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestTaskPayloadType.WASTE_QDR_APPLICATION_AMENDS_SUBMIT_PAYLOAD)")
    @Mapping(target = "reviewDecision", source = "requestPayload.reviewDecision", qualifiedByName = "reviewDecisionForOperatorAmend")
    WasteQDRApplicationAmendsSubmitRequestTaskPayload toWasteQDRApplicationAmendsSubmitRequestTaskPayload(
            WasteQDRRequestPayload requestPayload, WasteQDRRequestMetaData metadata);

    @Named("reviewDecisionForOperatorAmend")
    default WasteQDRReviewDecision setReviewDecisionForOperatorAmend(WasteQDRReviewDecision reviewDecision) {
        if (reviewDecision == null || reviewDecision.getType() == null) {
            return reviewDecision;
        }
        if (WasteQDRReviewDecisionType.OPERATOR_AMENDS_NEEDED == reviewDecision.getType()) {
            WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails originalDetails =
                    (WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails) reviewDecision.getDetails();

            WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails newDetails =
                    WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                            .requiredChanges(originalDetails.getRequiredChanges())
                            .notes(originalDetails.getNotes())
                            .build();

            return WasteQDRReviewDecision.builder()
                    .type(WasteQDRReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                    .details(newDetails)
                    .build();
        } else if (WasteQDRReviewDecisionType.ACCEPTED == reviewDecision.getType()) {
            WasteQDRReviewAcceptedDecisionDetails originalDetails =
                    (WasteQDRReviewAcceptedDecisionDetails) reviewDecision.getDetails();

            WasteQDRReviewAcceptedDecisionDetails newDetails = WasteQDRReviewAcceptedDecisionDetails.builder()
                    .notes(originalDetails.getNotes())
                    .build();

            return WasteQDRReviewDecision.builder()
                    .type(WasteQDRReviewDecisionType.ACCEPTED)
                    .details(newDetails)
                    .build();
        }
        return reviewDecision;
    }
}
