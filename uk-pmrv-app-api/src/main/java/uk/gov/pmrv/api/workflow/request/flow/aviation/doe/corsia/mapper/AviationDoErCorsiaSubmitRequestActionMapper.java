package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationDoErCorsiaSubmitRequestActionMapper {

    @Mapping(target = "doe.determinationReason.furtherDetails", ignore = true)
    @Mapping(target = "doe.fee.feeDetails.comments", ignore = true)
    AviationDoECorsiaSubmittedRequestActionPayload cloneCompletedPayloadIgnoreFurtherDetailsComments(
            AviationDoECorsiaSubmittedRequestActionPayload payload);
}
