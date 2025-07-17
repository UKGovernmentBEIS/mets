package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmittedRequestActionPayload;

import java.util.Set;

@Service
public class WithholdingOfAllowancesApplicationSubmittedCustomMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final WithholdingOfAllowancesRequestActionPayloadMapper requestActionPayloadMapper = Mappers
        .getMapper(WithholdingOfAllowancesRequestActionPayloadMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        WithholdingOfAllowancesApplicationSubmittedRequestActionPayload actionPayload =
            (WithholdingOfAllowancesApplicationSubmittedRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        WithholdingOfAllowancesApplicationSubmittedRequestActionPayload dtoPayload = requestActionPayloadMapper
            .cloneWithoutRegulatorComments(actionPayload);

        requestActionDTO.setPayload(dtoPayload);
        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
