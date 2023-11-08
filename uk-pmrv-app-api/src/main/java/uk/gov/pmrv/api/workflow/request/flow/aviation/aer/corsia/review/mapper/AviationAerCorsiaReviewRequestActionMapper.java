package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationCompletedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAerCorsiaReviewRequestActionMapper {

    @Mapping(target = "reviewGroupDecisions", ignore = true)
    AviationAerCorsiaApplicationCompletedRequestActionPayload cloneCompletedPayloadIgnoreDecisions(
        AviationAerCorsiaApplicationCompletedRequestActionPayload payload);
}
