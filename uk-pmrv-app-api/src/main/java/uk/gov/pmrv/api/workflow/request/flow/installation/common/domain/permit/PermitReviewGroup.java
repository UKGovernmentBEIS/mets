package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit;

import java.util.Set;

public enum PermitReviewGroup {

	PERMIT_TYPE,
	
	INSTALLATION_DETAILS,
	FUELS_AND_EQUIPMENT,
	DEFINE_MONITORING_APPROACHES,
	CALCULATION_CO2,
    MEASUREMENT_CO2,
    FALLBACK,
    MEASUREMENT_N2O,
    CALCULATION_PFC,
    INHERENT_CO2,
    TRANSFERRED_CO2_N2O,
	UNCERTAINTY_ANALYSIS,
	MANAGEMENT_PROCEDURES,
	MONITORING_METHODOLOGY_PLAN,
	ADDITIONAL_INFORMATION,
    CONFIDENTIALITY_STATEMENT,
    
    ;
	
	public static Set<PermitReviewGroup> getStandardReviewGroups(){
		return Set.of(
				PermitReviewGroup.PERMIT_TYPE,
				PermitReviewGroup.INSTALLATION_DETAILS,
				PermitReviewGroup.FUELS_AND_EQUIPMENT,
				PermitReviewGroup.DEFINE_MONITORING_APPROACHES, 
				PermitReviewGroup.UNCERTAINTY_ANALYSIS,
				PermitReviewGroup.MANAGEMENT_PROCEDURES,
				PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, 
				PermitReviewGroup.ADDITIONAL_INFORMATION,
				PermitReviewGroup.CONFIDENTIALITY_STATEMENT 
				);
	}
}
