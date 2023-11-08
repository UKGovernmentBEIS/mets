package uk.gov.pmrv.api.migration.emp.ukets.latesubmission;

import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.applicationtimeframe.EmpApplicationTimeframeInfo;

public class EmpApplicationTimeframeInfoMigrationMapper {

	public static EmpApplicationTimeframeInfo toEmpApplicationTimeframeInfo(
			EtsEmpApplicationTimeframeInfo etsEmpApplicationTimeframeInfo) {
		
		return EmpApplicationTimeframeInfo.builder()
                .dateOfStart(etsEmpApplicationTimeframeInfo.getDateAviationActivityCaptured() != null 
                	? etsEmpApplicationTimeframeInfo.getDateAviationActivityCaptured()
                		: etsEmpApplicationTimeframeInfo.getFldFirstFlyDate())
                .submittedOnTime(Boolean.valueOf(etsEmpApplicationTimeframeInfo.getSubmissionLateYes()))
                .reasonForLateSubmission(Boolean.FALSE.equals(Boolean.valueOf(etsEmpApplicationTimeframeInfo.getSubmissionLateYes())) 
                		&& etsEmpApplicationTimeframeInfo.getSubmissionLateJustify() == null 
                		? "N/A" 
                				: etsEmpApplicationTimeframeInfo.getSubmissionLateJustify())
                .build();	
	}
}
