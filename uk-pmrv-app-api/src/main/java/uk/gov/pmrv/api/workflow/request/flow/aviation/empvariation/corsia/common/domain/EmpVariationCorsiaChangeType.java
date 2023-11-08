package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmpVariationCorsiaChangeType {

	EMISSION_FACTOR_VALUES,
	FUEL_USE_MONITORING_METHODOLOGY,
	FUMM_TO_CERT,
	NEW_FUEL_TYPE,
	STATUS_FOR_THRESHOLDS,
	PARENT_SUBSIDIARY_RELATIONSHIP,
	
	AVAILABILITY_OF_DATA,
	INFORMATION_FOUND_INCORRECT,
	RECOMMENDATIONS_IN_VERIFICATION_IMPROVEMENT_REPORT,
	
	AEROPLANE_OPERATOR_NAME,
	REGISTERED_OR_CONTACT_ADDRESS,
	OTHER
}
