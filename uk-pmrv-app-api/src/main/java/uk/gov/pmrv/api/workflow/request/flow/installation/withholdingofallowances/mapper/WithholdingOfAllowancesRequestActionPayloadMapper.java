package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface WithholdingOfAllowancesRequestActionPayloadMapper {

    @Mapping(target = "withholdingOfAllowances.regulatorComments", ignore = true)
    WithholdingOfAllowancesApplicationSubmittedRequestActionPayload cloneWithoutRegulatorComments(
        WithholdingOfAllowancesApplicationSubmittedRequestActionPayload requestActionPayload);
}
