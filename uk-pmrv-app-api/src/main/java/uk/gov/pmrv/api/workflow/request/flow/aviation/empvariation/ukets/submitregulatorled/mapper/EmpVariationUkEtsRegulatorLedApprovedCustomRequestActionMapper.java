package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.mapper;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload;

@Component
public class EmpVariationUkEtsRegulatorLedApprovedCustomRequestActionMapper implements RequestActionCustomMapper {

	private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final EmpVariationUkEtsSubmitRegulatorLedMapper empVariationUkEtsSubmitRegulatorLedMapper = Mappers
            .getMapper(EmpVariationUkEtsSubmitRegulatorLedMapper.class);
    
	@Override
	public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
		final EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload requestActionPayload = (EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload) requestAction
				.getPayload();
		final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);
		final EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload dtoPayload = empVariationUkEtsSubmitRegulatorLedMapper
				.cloneRegulatorLedApprovedPayloadIgnoreDecisionNotes(requestActionPayload);

		requestActionDTO.setPayload(dtoPayload);
		return requestActionDTO;
	}

	@Override
	public RequestActionType getRequestActionType() {
		return RequestActionType.EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED;
	}

	@Override
	public Set<RoleType> getUserRoleTypes() {
		return Set.of(RoleType.OPERATOR, RoleType.VERIFIER);
	}

}
