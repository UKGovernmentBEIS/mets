package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationDeemedWithdrawnRequestActionPayload;

import java.util.Set;

@Component
public class PermitVariationDeemedWithdrawnCustomRequestActionMapper implements RequestActionCustomMapper {
    
    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final PermitVariationReviewRequestActionMapper permitVariationReviewRequestActionMapper = Mappers
            .getMapper(PermitVariationReviewRequestActionMapper.class);
    

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        final PermitVariationApplicationDeemedWithdrawnRequestActionPayload entityPayload =
            (PermitVariationApplicationDeemedWithdrawnRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        final PermitVariationApplicationDeemedWithdrawnRequestActionPayload dtoPayload = 
        		permitVariationReviewRequestActionMapper.cloneDeemedWithdrawnPayloadIgnoreReason(entityPayload);

        requestActionDTO.setPayload(dtoPayload);
        
        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}