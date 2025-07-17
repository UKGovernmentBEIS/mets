package uk.gov.pmrv.api.workflow.request.flow.installation.vir.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationReviewedRequestActionPayload;

import java.util.Set;

@Service
public class VirApplicationReviewedCustomMapper implements RequestActionCustomMapper {

    private static final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);
    private static final VirMapper virMapper = Mappers.getMapper(VirMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        final VirApplicationReviewedRequestActionPayload entityPayload =
                (VirApplicationReviewedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);
        final VirApplicationReviewedRequestActionPayload dtoPayload = virMapper.cloneReviewedPayloadIgnoreComments(entityPayload);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.VIR_APPLICATION_REVIEWED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
