package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmittedRequestActionPayload;

public interface EmpVariationUkEtsOperatorDetailsMapper {
	
	@AfterMapping
    default void setOperatorDetailsOperatorNameAndCrcoCode(@MappingTarget EmissionsMonitoringPlanUkEts target,
    		String operatorName, String crcoCode) {
		target.getOperatorDetails().setOperatorName(operatorName);
		target.getOperatorDetails().setCrcoCode(crcoCode);
    }
	
	@AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget EmissionsMonitoringPlanUkEtsContainer container,
                                            RequestAviationAccountInfo accountInfo) {
        EmpOperatorDetails operatorDetails = container.getEmissionsMonitoringPlan().getOperatorDetails();
        operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
    }
	
	@AfterMapping
    default void setOperatorDetailsCrcoCode(@MappingTarget EmpVariationUkEtsApplicationRequestTaskPayload requestTaskPayload,
                                            RequestAviationAccountInfo aviationAccountInfo) {
        EmpOperatorDetails operatorDetails = requestTaskPayload.getEmissionsMonitoringPlan().getOperatorDetails();
        operatorDetails.setCrcoCode(aviationAccountInfo.getCrcoCode());
    }
	
	@AfterMapping
	default void setOperatorDetailsCrcoCode(
			@MappingTarget EmpVariationUkEtsApplicationSubmittedRequestActionPayload submittedRequestActionPayload,
			RequestAviationAccountInfo accountInfo) {
		EmpOperatorDetails operatorDetails = submittedRequestActionPayload.getEmissionsMonitoringPlan()
				.getOperatorDetails();
		operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
	}
	
	@AfterMapping
    default void setOperatorDetailsNameAndCrcoCode(@MappingTarget EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload requestActionPayload,
                                                   RequestAviationAccountInfo accountInfo) {
        EmpOperatorDetails operatorDetails = requestActionPayload.getEmissionsMonitoringPlan().getOperatorDetails();
        operatorDetails.setOperatorName(accountInfo.getOperatorName());
        operatorDetails.setCrcoCode(accountInfo.getCrcoCode());
    }
}
