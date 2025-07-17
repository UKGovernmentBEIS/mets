package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationCompletedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAerUkEtsReviewRequestActionMapper {

    @Mapping(target = "reviewGroupDecisions", ignore = true)
    AviationAerUkEtsApplicationCompletedRequestActionPayload cloneCompletedPayloadIgnoreDecisions(
        AviationAerUkEtsApplicationCompletedRequestActionPayload payload);
}
