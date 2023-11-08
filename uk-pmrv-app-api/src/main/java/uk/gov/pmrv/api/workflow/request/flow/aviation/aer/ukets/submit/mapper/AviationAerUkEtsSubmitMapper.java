package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAerUkEtsSubmitMapper {

    AviationAerUkEtsContainer toAviationAerUkEtsContainer(AviationAerUkEtsApplicationSubmitRequestTaskPayload taskPayload, EmissionTradingScheme scheme);

    AviationAerUkEtsContainer toAviationAerUkEtsContainer(AviationAerUkEtsApplicationSubmitRequestTaskPayload taskPayload,
                                                          AviationAerUkEtsVerificationReport verificationReport,
                                                          EmissionTradingScheme scheme);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "serviceContactDetails", source = "accountInfo.serviceContactDetails")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "aerAttachments", ignore = true)
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerUkEtsApplicationSubmittedRequestActionPayload toAviationAerUkEtsApplicationSubmittedRequestActionPayload(
        AviationAerUkEtsApplicationSubmitRequestTaskPayload taskPayload, RequestAviationAccountInfo accountInfo, RequestActionPayloadType payloadType);

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
                                   AviationAerUkEtsApplicationSubmitRequestTaskPayload taskPayload) {
        requestActionPayload.setAerAttachments(taskPayload.getAerAttachments());
    }
}
