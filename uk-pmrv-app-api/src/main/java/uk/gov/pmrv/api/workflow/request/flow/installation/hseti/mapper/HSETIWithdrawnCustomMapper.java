package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class HSETIWithdrawnCustomMapper implements RequestActionCustomMapper {

    private final HSETICompletedCustomMapper hsetiCompletedCustomMapper;

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        return hsetiCompletedCustomMapper.toRequestActionDTO(requestAction);
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.HSE_TI_WITHDRAWN;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}
