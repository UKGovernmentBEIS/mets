package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationCompletedRequestActionPayload;

@Service
public class AviationAerCorsiaApplicationCompletedCustomMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);
    private final AviationAerCorsiaReviewRequestActionMapper aerReviewRequestActionMapper = Mappers.getMapper(AviationAerCorsiaReviewRequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        AviationAerCorsiaApplicationCompletedRequestActionPayload entityPayload =
            (AviationAerCorsiaApplicationCompletedRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        AviationAerCorsiaApplicationCompletedRequestActionPayload dtoPayload =
            aerReviewRequestActionMapper.cloneCompletedPayloadIgnoreDecisions(entityPayload);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.AVIATION_AER_CORSIA_APPLICATION_COMPLETED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
