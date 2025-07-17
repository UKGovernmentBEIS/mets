package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload;

import java.util.Set;

@Service
public class EmpIssuanceUkEtsApplicationDeemedWithdrawnCustomMapper implements RequestActionCustomMapper {

    private static final RequestActionMapper REQUEST_ACTION_MAPPER = Mappers.getMapper(RequestActionMapper.class);

    private static final EmpIssuanceUkEtsReviewRequestActionMapper EMP_ISSUANCE_UKETS_REVIEW_REQUEST_ACTION_MAPPER =
        Mappers.getMapper(EmpIssuanceUkEtsReviewRequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload requestActionPayload =
            (EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = REQUEST_ACTION_MAPPER.toRequestActionDTOIgnorePayload(requestAction);

        EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload clonedRequestActionPayload =
            EMP_ISSUANCE_UKETS_REVIEW_REQUEST_ACTION_MAPPER.cloneDeemedWithdrawnPayloadIgnoreReason(requestActionPayload);

        requestActionDTO.setPayload(clonedRequestActionPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
