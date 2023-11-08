package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.mapper;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationRejectedRequestActionPayload;

@Service
public class EmpVariationUkEtsApplicationRejectedCustomMapper implements RequestActionCustomMapper {

	private static final RequestActionMapper REQUEST_ACTION_MAPPER = Mappers.getMapper(RequestActionMapper.class);

    private static final EmpVariationUkEtsReviewRequestActionMapper EMP_VARIATION_UKETS_REVIEW_REQUEST_ACTION_MAPPER =
        Mappers.getMapper(EmpVariationUkEtsReviewRequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        EmpVariationUkEtsApplicationRejectedRequestActionPayload requestActionPayload =
            (EmpVariationUkEtsApplicationRejectedRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = REQUEST_ACTION_MAPPER.toRequestActionDTOIgnorePayload(requestAction);

        EmpVariationUkEtsApplicationRejectedRequestActionPayload clonedRequestActionPayload =
        		EMP_VARIATION_UKETS_REVIEW_REQUEST_ACTION_MAPPER.cloneRejectedPayloadIgnoreReason(requestActionPayload);

        requestActionDTO.setPayload(clonedRequestActionPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.EMP_VARIATION_UKETS_APPLICATION_REJECTED;
    }

    @Override
    public Set<RoleType> getUserRoleTypes() {
        return Set.of(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
