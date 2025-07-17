package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAerCorsiaSubmitMapper {

    AviationAerCorsiaContainer toAviationAerCorsiaContainer(AviationAerCorsiaApplicationSubmitRequestTaskPayload taskPayload, EmissionTradingScheme scheme);

    AviationAerCorsiaContainer toAviationAerCorsiaContainer(AviationAerCorsiaApplicationSubmitRequestTaskPayload taskPayload, AviationAerCorsiaVerificationReport verificationReport, EmissionTradingScheme scheme);


    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "aerAttachments", ignore = true)
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerCorsiaApplicationSubmittedRequestActionPayload toAviationAerCorsiaApplicationSubmittedRequestActionPayload(
            AviationAerCorsiaApplicationSubmitRequestTaskPayload taskPayload,
            RequestAviationAccountInfo accountInfo,
            AviationAerCorsiaSubmittedEmissions submittedEmissions,
            RequestActionPayloadType payloadType);

    @AfterMapping
    default void setAerAttachments(@MappingTarget AviationAerCorsiaApplicationSubmittedRequestActionPayload requestActionPayload,
                                   AviationAerCorsiaApplicationSubmitRequestTaskPayload taskPayload) {
        requestActionPayload.setAerAttachments(taskPayload.getAerAttachments());
    }

}
