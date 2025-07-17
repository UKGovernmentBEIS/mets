package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmittedRequestActionPayload;

import java.util.Set;

@Service
public class ReturnOfAllowancesApplicationSubmittedCustomMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final ReturnOfAllowancesRequestActionPayloadMapper requestActionPayloadMapper = Mappers
        .getMapper(ReturnOfAllowancesRequestActionPayloadMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        ReturnOfAllowancesApplicationSubmittedRequestActionPayload actionPayload =
            (ReturnOfAllowancesApplicationSubmittedRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        ReturnOfAllowancesApplicationSubmittedRequestActionPayload dtoPayload = requestActionPayloadMapper
            .cloneWithoutRegulatorComments(actionPayload);

        requestActionDTO.setPayload(dtoPayload);
        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
