package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationRejectedRequestActionPayload;

@Component
public class PermitVariationRejectedCustomRequestActionMapper implements RequestActionCustomMapper {
    
    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final PermitVariationReviewRequestActionMapper permitVariationReviewRequestActionMapper = Mappers
            .getMapper(PermitVariationReviewRequestActionMapper.class);
    

    @Override
    public RequestActionDTO toRequestActionDTO(final RequestAction requestAction) {
        final PermitVariationApplicationRejectedRequestActionPayload entityPayload =
            (PermitVariationApplicationRejectedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        final PermitVariationApplicationRejectedRequestActionPayload dtoPayload = 
        		permitVariationReviewRequestActionMapper.cloneRejectedPayloadIgnoreReason(entityPayload);

        requestActionDTO.setPayload(dtoPayload);
        
        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMIT_VARIATION_APPLICATION_REJECTED;
    }

    @Override
    public Set<RoleType> getUserRoleTypes() {
        return Set.of(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
