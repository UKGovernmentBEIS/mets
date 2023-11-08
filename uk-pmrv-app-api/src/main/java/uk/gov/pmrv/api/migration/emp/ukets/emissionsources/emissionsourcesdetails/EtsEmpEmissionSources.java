package uk.gov.pmrv.api.migration.emp.ukets.emissionsources.emissionsourcesdetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpEmissionSources {

	private String fldEmitterId;
	private String fldEmitterDisplayId;
	private String approachJustification;
	
	private String procedureDetailsAdditionalAircraftTitle;
	private String procedureDetailsAdditionalAircraftReference;
	private String procedureDetailsAdditionalAircraftDescription;
	private String procedureDetailsAdditionalAircraftPost;
	private String procedureDetailsAdditionalAircraftLocation;
	private String procedureDetailsAdditionalAircraftSystem;
}
