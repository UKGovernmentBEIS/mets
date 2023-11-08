package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmpVariationUkEtsChangeType {

	EMISSION_FACTOR_VALUES,
	METHOD_USED_TO_CALCULATE_EMISSIONS_FACTOR,
	DIFFERENT_FUMMS,
	FUMM_TO_ESTIMATION_METHOD,
	NEW_FUEL_TYPE,
	SMALL_EMITTER_STATUS_OR_EMISSIONS_THRESHOLD_STATUS,
	
	LEGAL_ENTITY_NAME,
	REGISTERED_OFFICE_ADDRESS,
	OTHER
	
}
