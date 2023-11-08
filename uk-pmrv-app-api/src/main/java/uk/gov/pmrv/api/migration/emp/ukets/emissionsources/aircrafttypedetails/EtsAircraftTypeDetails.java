package uk.gov.pmrv.api.migration.emp.ukets.emissionsources.aircrafttypedetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsAircraftTypeDetails {

	private String fldEmitterId;
	private String fldEmitterDisplayId;
	private String genericAircraftType;
	private String hMake;
	private String hModel;
	private String hDesignator;
	private String genericAircraftSubtype;
	private Long numberOfAircraft;
	private Boolean chkJetKerosene;
	private Boolean chkJetGasoline;
	private Boolean chkAviationGasoline;
	private String methodology;
}
