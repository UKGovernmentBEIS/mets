package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.mapper;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload;

@Component
public class EmpVariationCorsiaRegulatorLedApprovedCustomRequestActionMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);
    private static final EmpVariationCorsiaSubmitRegulatorLedMapper MAPPER = 
        Mappers.getMapper(EmpVariationCorsiaSubmitRegulatorLedMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {

        final EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload requestActionPayload =
            (EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload) requestAction.getPayload();
        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);
        final EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload dtoPayload =
            MAPPER.cloneRegulatorLedApprovedPayloadIgnoreDecisionNotes(requestActionPayload);

        requestActionDTO.setPayload(dtoPayload);
        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED;
    }

    @Override
    public Set<String> getUserRoleTypes() {
        return Set.of(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }

}
