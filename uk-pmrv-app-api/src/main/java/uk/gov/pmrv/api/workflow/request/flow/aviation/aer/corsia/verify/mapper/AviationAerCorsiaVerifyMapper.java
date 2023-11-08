package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;

import java.math.BigDecimal;
import java.time.Year;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAerCorsiaVerifyMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "serviceContactDetails", source = "aviationAccountInfo.serviceContactDetails")
    @Mapping(target = "totalEmissionsProvided", source = "totalEmissionsProvided")
    @Mapping(target = "totalOffsetEmissionsProvided", source = "totalOffsetEmissionsProvided")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "aerAttachments", ignore = true)
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload toAviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload(
            AviationAerCorsiaRequestPayload requestPayload,
            AviationAerCorsiaVerificationReport verificationReport,
            RequestAviationAccountInfo aviationAccountInfo,
            BigDecimal totalEmissionsProvided,
            BigDecimal totalOffsetEmissionsProvided,
            Year reportingYear,
            RequestTaskPayloadType payloadType);

    @AfterMapping
    default void setAerAttachments(@MappingTarget AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload,
                                   AviationAerCorsiaRequestPayload requestPayload) {
        verificationSubmitRequestTaskPayload.setAerAttachments(requestPayload.getAerAttachments());
    }

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "aviationAccountInfo.serviceContactDetails")
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload toAviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload(
        AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload requestTaskPayload,
        RequestAviationAccountInfo aviationAccountInfo,
        RequestActionPayloadType payloadType
    );
}
