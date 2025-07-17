package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAerCorsiaAnnualOffsettingMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMITTED_PAYLOAD)")
    AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload toSubmittedActionPayload(AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload);
}
