package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload;

public interface EmpVariationCorsiaOperatorDetailsMapper {
	
	@AfterMapping
    default void setOperatorDetailsOperatorName(@MappingTarget EmissionsMonitoringPlanCorsia target,
    		String operatorName) {
		target.getOperatorDetails().setOperatorName(operatorName);
    }

	@AfterMapping
	default void setOperatorDetailsOperatorName(
		@MappingTarget EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload requestActionPayload,
		RequestAviationAccountInfo accountInfo) {
		EmpCorsiaOperatorDetails operatorDetails = requestActionPayload.getEmissionsMonitoringPlan()
			.getOperatorDetails();
		operatorDetails.setOperatorName(accountInfo.getOperatorName());
	}
}
