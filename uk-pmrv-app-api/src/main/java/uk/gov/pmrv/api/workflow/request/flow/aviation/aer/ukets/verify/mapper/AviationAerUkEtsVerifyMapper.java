package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;

import java.math.BigDecimal;
import java.time.Year;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAerUkEtsVerifyMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "serviceContactDetails", source = "aviationAccountInfo.serviceContactDetails")
    @Mapping(target = "notCoveredChangesProvided", source = "requestPayload.aer.aerMonitoringPlanChanges.details")
    @Mapping(target = "totalEmissionsProvided", source = "totalEmissionsProvided")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "aerAttachments", ignore = true)
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload toAviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload(
        AviationAerUkEtsRequestPayload requestPayload,
        AviationAerUkEtsVerificationReport verificationReport,
        RequestAviationAccountInfo aviationAccountInfo,
        BigDecimal totalEmissionsProvided,
        Year reportingYear,
        RequestTaskPayloadType payloadType);

    @AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload,
                                            RequestAviationAccountInfo accountInfo) {
        AviationOperatorDetails operatorDetails = verificationSubmitRequestTaskPayload.getAer().getOperatorDetails();
        operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
    }

    @AfterMapping
    default void setAerAttachments(@MappingTarget AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload,
                                  AviationAerUkEtsRequestPayload requestPayload) {
        verificationSubmitRequestTaskPayload.setAerAttachments(requestPayload.getAerAttachments());
    }

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "aviationAccountInfo.serviceContactDetails")
    @Mapping(target = "aer.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload toAviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload(
        AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload requestTaskPayload,
        RequestAviationAccountInfo aviationAccountInfo,
        RequestActionPayloadType payloadType
    );

    @AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload verificationSubmittedRequestActionPayload,
                                            RequestAviationAccountInfo accountInfo) {
        AviationOperatorDetails operatorDetails = verificationSubmittedRequestActionPayload.getAer().getOperatorDetails();
        operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
    }
}
