package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAerCorsiaMapper {

    @Mapping(target = "reportingYear", source = "requestMetadata.year")
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerCorsiaContainer toAviationAerCorsiaContainer(AviationAerCorsiaRequestPayload requestPayload,
                                                            EmissionTradingScheme scheme,
                                                            RequestAviationAccountInfo accountInfo,
                                                            AviationAerCorsiaRequestMetadata requestMetadata);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reportingYear", source = "requestMetadata.year")
    @Mapping(target = "aer.operatorDetails.attachmentIds", ignore = true)
    @Mapping(target = "aer.aerSectionAttachmentIds", ignore = true)
    AviationAerCorsiaApplicationCompletedRequestActionPayload toAviationAerCorsiaApplicationCompletedRequestActionPayload(
        AviationAerCorsiaRequestPayload requestPayload, RequestActionPayloadType payloadType,
        RequestAviationAccountInfo accountInfo, AviationAerCorsiaRequestMetadata requestMetadata);
}
