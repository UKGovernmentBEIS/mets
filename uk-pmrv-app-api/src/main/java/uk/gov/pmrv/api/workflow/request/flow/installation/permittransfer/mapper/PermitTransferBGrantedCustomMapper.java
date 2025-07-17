package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationGrantedRequestActionPayload;

import java.util.Set;

@Service
public class PermitTransferBGrantedCustomMapper implements RequestActionCustomMapper {


    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final PermitTransferMapper permitTransferMapper = Mappers.getMapper(PermitTransferMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(final RequestAction requestAction) {

        final PermitTransferBApplicationGrantedRequestActionPayload entityPayload =
            (PermitTransferBApplicationGrantedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        final PermitTransferBApplicationGrantedRequestActionPayload dtoPayload =
            permitTransferMapper.cloneGrantedPayloadIgnoreReasonAndDecisions(entityPayload);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMIT_TRANSFER_B_APPLICATION_GRANTED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
