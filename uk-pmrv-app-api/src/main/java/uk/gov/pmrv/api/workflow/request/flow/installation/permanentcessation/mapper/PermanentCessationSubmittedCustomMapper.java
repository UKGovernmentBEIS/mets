package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.mapper;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;

import java.util.Set;

@Service
public class PermanentCessationSubmittedCustomMapper implements RequestActionCustomMapper {

    private final PermanentCessationApplicationSubmittedCustomMapper requestActionMapper
            = new PermanentCessationApplicationSubmittedCustomMapper();



    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        return requestActionMapper.toRequestActionDTO(requestAction);
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMANENT_CESSATION_APPLICATION_SUBMITTED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
