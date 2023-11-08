package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload;

import java.util.Set;

@Service
public class ReturnOfAllowancesReturnedApplicationCompletedCustomMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final ReturnOfAllowancesRequestActionPayloadMapper requestActionPayloadMapper = Mappers
        .getMapper(ReturnOfAllowancesRequestActionPayloadMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload actionPayload =
            (ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload dtoPayload = requestActionPayloadMapper
            .cloneWithoutRegulatorComments(actionPayload);

        requestActionDTO.setPayload(dtoPayload);
        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED;
    }

    @Override
    public Set<RoleType> getUserRoleTypes() {
        return Set.of(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
