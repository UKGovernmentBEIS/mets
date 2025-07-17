package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.mapper;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationRejectedRequestActionPayload;

@Service
public class EmpVariationCorsiaApplicationRejectedCustomMapper implements RequestActionCustomMapper {

	private static final RequestActionMapper REQUEST_ACTION_MAPPER = Mappers.getMapper(RequestActionMapper.class);

    private static final EmpVariationCorsiaReviewRequestActionMapper EMP_VARIATION_CORSIA_REVIEW_REQUEST_ACTION_MAPPER =
        Mappers.getMapper(EmpVariationCorsiaReviewRequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        EmpVariationCorsiaApplicationRejectedRequestActionPayload requestActionPayload =
            (EmpVariationCorsiaApplicationRejectedRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = REQUEST_ACTION_MAPPER.toRequestActionDTOIgnorePayload(requestAction);

        EmpVariationCorsiaApplicationRejectedRequestActionPayload clonedRequestActionPayload =
        		EMP_VARIATION_CORSIA_REVIEW_REQUEST_ACTION_MAPPER.cloneRejectedPayloadIgnoreReason(requestActionPayload);

        requestActionDTO.setPayload(clonedRequestActionPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_REJECTED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
