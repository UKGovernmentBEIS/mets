package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAerUkEtsMapper {

    @Mapping(target = "reportingYear", source = "requestMetadata.year")
    @Mapping(target = "aer.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerUkEtsContainer toAviationAerUkEtsContainer(AviationAerUkEtsRequestPayload requestPayload,
                                                          EmissionTradingScheme scheme,
                                                          RequestAviationAccountInfo accountInfo,
                                                          AviationAerRequestMetadata requestMetadata);

    @AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget AviationAerUkEtsContainer aerContainer,
                                            RequestAviationAccountInfo accountInfo) {
        AviationAerUkEts aer = aerContainer.getAer();
        //if statement has been added for the case where the operator has no reporting obligation, thus aer will be null
        if(aer != null) {
            AviationOperatorDetails operatorDetails = aer.getOperatorDetails();
            operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
        }
    }

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reportingYear", source = "requestMetadata.year")
    @Mapping(target = "aer.operatorDetails.crcoCode", ignore = true)
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerUkEtsApplicationCompletedRequestActionPayload toAviationAerUkEtsApplicationCompletedRequestActionPayload(
        AviationAerUkEtsRequestPayload requestPayload, RequestActionPayloadType payloadType,
        RequestAviationAccountInfo accountInfo, AviationAerRequestMetadata requestMetadata);

    @AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget AviationAerUkEtsApplicationCompletedRequestActionPayload requestActionPayload,
                                            RequestAviationAccountInfo accountInfo) {
        AviationAerUkEts aer = requestActionPayload.getAer();
        //if statement has been added for the case where the operator has no reporting obligation, thus aer will be null
        if (aer != null) {
            AviationOperatorDetails operatorDetails = aer.getOperatorDetails();
            operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
        }
    }
}
