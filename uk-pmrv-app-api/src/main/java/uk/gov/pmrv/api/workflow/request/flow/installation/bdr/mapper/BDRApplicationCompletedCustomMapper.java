package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationCompletedRequestActionPayload;
import java.util.Set;

@Service
public class BDRApplicationCompletedCustomMapper  implements RequestActionCustomMapper {

     private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {

        final BDRApplicationCompletedRequestActionPayload entityPayload =
                (BDRApplicationCompletedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        if (!ObjectUtils.isEmpty(entityPayload.getRegulatorReviewOutcome())) {

            entityPayload.getRegulatorReviewOutcome().setUseHseNotes(null);
            entityPayload.getRegulatorReviewOutcome().setFreeAllocationNotes(null);
        }

        if(!ObjectUtils.isEmpty(entityPayload.getVerificationReport())) {
            entityPayload.getVerificationReport().getVerificationData().getOpinionStatement().setNotes(null);
        }

        requestActionDTO.setPayload(entityPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.BDR_APPLICATION_COMPLETED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR);
    }
}
