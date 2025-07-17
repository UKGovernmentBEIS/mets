package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface ReturnOfAllowancesRequestActionPayloadMapper {

    @Mapping(target = "returnOfAllowances.regulatorComments", ignore = true)
    ReturnOfAllowancesApplicationSubmittedRequestActionPayload cloneWithoutRegulatorComments(
        ReturnOfAllowancesApplicationSubmittedRequestActionPayload requestActionPayload);

    @Mapping(target = "returnOfAllowancesReturned.regulatorComments", ignore = true)
    ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload cloneWithoutRegulatorComments(
        ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload requestActionPayload);
}
