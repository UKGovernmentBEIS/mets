package uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationCompletedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AerReviewRequestActionMapper {

    @Mapping(target = "reviewGroupDecisions", ignore = true)
    @Mapping(target = "aerSkipReviewDecision", ignore = true)
    AerApplicationCompletedRequestActionPayload cloneCompletedPayloadIgnoreDecisions(
            AerApplicationCompletedRequestActionPayload payload);
}
