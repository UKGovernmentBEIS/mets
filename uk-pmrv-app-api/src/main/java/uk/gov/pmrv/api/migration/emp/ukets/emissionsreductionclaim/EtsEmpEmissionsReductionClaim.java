package uk.gov.pmrv.api.migration.emp.ukets.emissionsreductionclaim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpEmissionsReductionClaim {

	private String fldEmitterId;
	private String fldEmitterDisplayId;
	
	private String purchaseDeliveryTitle;
	private String purchaseDeliveryReference;
	private String purchaseDeliveryDescription;
	private String purchaseDeliveryPost;
	private String purchaseDeliveryLocation;
	private String purchaseDeliverySystem;
	
	private String sustainabilityTitle;
	private String sustainabilityReference;
	private String sustainabilityDescription;
	private String sustainabilityPost;
	private String sustainabilityLocation;
	private String sustainabilitySystem;
	
	private String avoidanceTitle;
	private String avoidanceReference;
	private String avoidanceDescription;
	private String avoidancePost;
	private String avoidanceLocation;
	private String avoidanceSystem;
}
