package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationRegulatorLedApprovedRequestActionPayload;

import java.util.Set;

@Component
public class PermitVariationRegulatorLedApprovedCustomRequestActionMapper implements RequestActionCustomMapper {
    
    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final PermitVariationRegulatorLedMapper permitVariationRegulatorLedMapper = Mappers
            .getMapper(PermitVariationRegulatorLedMapper.class);
    

    @Override
    public RequestActionDTO toRequestActionDTO(final RequestAction requestAction) {
        final PermitVariationApplicationRegulatorLedApprovedRequestActionPayload entityPayload =
            (PermitVariationApplicationRegulatorLedApprovedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        final PermitVariationApplicationRegulatorLedApprovedRequestActionPayload dtoPayload = 
        		permitVariationRegulatorLedMapper.cloneRegulatorLedApprovedPayloadIgnoreReasonAndDecisionNotes(entityPayload);

        requestActionDTO.setPayload(dtoPayload);
        
        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
