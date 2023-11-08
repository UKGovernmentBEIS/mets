package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationApprovedRequestActionPayload;

import java.util.Set;

@Service
public class EmpIssuanceUkEtsApplicationApprovedCustomMapper implements RequestActionCustomMapper {

    private static final RequestActionMapper REQUEST_ACTION_MAPPER = Mappers.getMapper(RequestActionMapper.class);

    private static final EmpIssuanceUkEtsReviewRequestActionMapper EMP_ISSUANCE_UKETS_REVIEW_REQUEST_ACTION_MAPPER =
        Mappers.getMapper(EmpIssuanceUkEtsReviewRequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        EmpIssuanceUkEtsApplicationApprovedRequestActionPayload requestActionPayload =
            (EmpIssuanceUkEtsApplicationApprovedRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = REQUEST_ACTION_MAPPER.toRequestActionDTOIgnorePayload(requestAction);

        EmpIssuanceUkEtsApplicationApprovedRequestActionPayload clonedRequestActionPayload =
            EMP_ISSUANCE_UKETS_REVIEW_REQUEST_ACTION_MAPPER.cloneApprovedPayloadIgnoreReasonAndDecisions(requestActionPayload);

        requestActionDTO.setPayload(clonedRequestActionPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.EMP_ISSUANCE_UKETS_APPLICATION_APPROVED;
    }

    @Override
    public Set<RoleType> getUserRoleTypes() {
        return Set.of(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
