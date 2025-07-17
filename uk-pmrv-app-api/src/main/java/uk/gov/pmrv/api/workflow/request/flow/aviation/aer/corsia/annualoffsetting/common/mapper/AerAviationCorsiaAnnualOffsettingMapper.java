package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AerAviationCorsiaAnnualOffsettingMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload toAviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload(
            AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload, RequestTaskPayloadType payloadType);

}
