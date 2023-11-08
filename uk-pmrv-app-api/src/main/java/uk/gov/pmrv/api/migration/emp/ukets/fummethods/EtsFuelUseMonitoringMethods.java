package uk.gov.pmrv.api.migration.emp.ukets.fummethods;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsFuelUseMonitoringMethods {

	private String fldEmitterId;
	private String fldEmitterDisplayId;
	
	private String procedureDetailsTitle;
	private String procedureDetailsReference;
	private String procedureDetailsDescription;
	private String procedureDetailsPost;
	private String procedureDetailsLocation;
	private String procedureDetailsSystem;
	
	private String densityMeasurementProcedureTitle;
	private String densityMeasurementProcedureReference;
	private String densityMeasurementProcedureDescription;
	private String densityMeasurementProcedurePost;
	private String densityMeasurementProcedureLocation;
	private String densityMeasurementProcedureSystem;
}
