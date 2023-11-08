package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.mapper;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload;

@Service
public class EmpIssuanceCorsiaApplicationDeemedWithdrawnCustomMapper implements RequestActionCustomMapper {

    private static final RequestActionMapper REQUEST_ACTION_MAPPER = Mappers.getMapper(RequestActionMapper.class);

    private static final EmpIssuanceCorsiaReviewRequestActionMapper
        EMP_ISSUANCE_CORSIA_REVIEW_REQUEST_ACTION_MAPPER =
        Mappers.getMapper(EmpIssuanceCorsiaReviewRequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload requestActionPayload =
            (EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload) requestAction.getPayload();

        RequestActionDTO requestActionDTO = REQUEST_ACTION_MAPPER.toRequestActionDTOIgnorePayload(requestAction);

        EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload clonedRequestActionPayload =
            EMP_ISSUANCE_CORSIA_REVIEW_REQUEST_ACTION_MAPPER.cloneDeemedWithdrawnPayloadIgnoreReason(requestActionPayload);

        requestActionDTO.setPayload(clonedRequestActionPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN;
    }

    @Override
    public Set<RoleType> getUserRoleTypes() {
        return Set.of(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
