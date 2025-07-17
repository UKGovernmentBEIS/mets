package uk.gov.pmrv.api.migration.emp.ukets.latesubmission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpApplicationTimeframeInfo {
	
	private String fldEmitterId;
	private String fldEmitterDisplayId;
	private LocalDate fldFirstFlyDate;
	private LocalDate dateAviationActivityCaptured;
	private String submissionLateYes;
	private String submissionLateJustify;

}
