package uk.gov.pmrv.api.migration.emp.ukets.latesubmission;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
