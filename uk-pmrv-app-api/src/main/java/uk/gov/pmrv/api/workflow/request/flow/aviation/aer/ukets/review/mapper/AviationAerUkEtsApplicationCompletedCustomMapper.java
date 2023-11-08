package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationCompletedRequestActionPayload;

import java.util.Set;

@Service
public class AviationAerUkEtsApplicationCompletedCustomMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);
    private final AviationAerUkEtsReviewRequestActionMapper aerReviewRequestActionMapper = Mappers.getMapper(AviationAerUkEtsReviewRequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        AviationAerUkEtsApplicationCompletedRequestActionPayload entityPayload =
            (AviationAerUkEtsApplicationCompletedRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        AviationAerUkEtsApplicationCompletedRequestActionPayload dtoPayload =
            aerReviewRequestActionMapper.cloneCompletedPayloadIgnoreDecisions(entityPayload);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.AVIATION_AER_UKETS_APPLICATION_COMPLETED;
    }

    @Override
    public Set<RoleType> getUserRoleTypes() {
        return Set.of(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
