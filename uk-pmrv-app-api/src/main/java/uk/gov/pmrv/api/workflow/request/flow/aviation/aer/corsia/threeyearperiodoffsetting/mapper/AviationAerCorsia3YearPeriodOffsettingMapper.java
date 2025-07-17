package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAerCorsia3YearPeriodOffsettingMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload toAviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload(
            AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload, RequestTaskPayloadType payloadType);


    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMITTED_PAYLOAD)")
    AviationAerCorsia3YearPeriodOffsettingApplicationSubmittedRequestActionPayload toSubmittedActionPayload(AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload);
}
