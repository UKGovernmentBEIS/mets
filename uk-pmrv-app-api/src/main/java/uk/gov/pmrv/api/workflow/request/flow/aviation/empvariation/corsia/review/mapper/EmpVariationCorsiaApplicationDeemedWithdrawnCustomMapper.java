package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.mapper;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload;

@Service
public class EmpVariationCorsiaApplicationDeemedWithdrawnCustomMapper implements RequestActionCustomMapper {

	private static final RequestActionMapper REQUEST_ACTION_MAPPER = Mappers.getMapper(RequestActionMapper.class);

    private static final EmpVariationCorsiaReviewRequestActionMapper EMP_VARIATION_CORSIA_REVIEW_REQUEST_ACTION_MAPPER =
        Mappers.getMapper(EmpVariationCorsiaReviewRequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload requestActionPayload =
            (EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = REQUEST_ACTION_MAPPER.toRequestActionDTOIgnorePayload(requestAction);

        EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload clonedRequestActionPayload =
        		EMP_VARIATION_CORSIA_REVIEW_REQUEST_ACTION_MAPPER.cloneDeemedWithdrawnPayloadIgnoreReason(requestActionPayload);

        requestActionDTO.setPayload(clonedRequestActionPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN;
    }

    @Override
    public Set<RoleType> getUserRoleTypes() {
        return Set.of(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
