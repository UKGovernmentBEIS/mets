package uk.gov.pmrv.api.migration.emp.corsia.fummethods;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsFuelUseMonitoringMethodsCorsia {
	
	private String fldEmitterID;
	private String fldEmitterDisplayID;
	private String corsiaApproachJustification;
	
	private String methodAProcedureTitle;
	private String methodAProcedureReference;
	private String methodAProcedureDescription;
	private String methodAProcedurePost;
	private String methodAProcedureLocation;
	private String methodAProcedureSystem;
	
	private String fuelDensityMethodAProcedureTitle;
	private String fuelDensityMethodAProcedureReference;
	private String fuelDensityMethodAProcedureDescription;
	private String fuelDensityMethodAProcedurePost;
	private String fuelDensityMethodAProcedureLocation;
	private String fuelDensityMethodAProcedureSystem;
	
	private String methodBProcedureTitle;
	private String methodBProcedureReference;
	private String methodBProcedureDescription;
	private String methodBProcedurePost;
	private String methodBProcedureLocation;
	private String methodBProcedureSystem;
	
	private String fuelDensityMethodBProcedureTitle;
	private String fuelDensityMethodBProcedureReference;
	private String fuelDensityMethodBProcedureDescription;
	private String fuelDensityMethodBProcedurePost;
	private String fuelDensityMethodBProcedureLocation;
	private String fuelDensityMethodBProcedureSystem;
	
	private String zeroFuelUpliftDescription;
	private String fuelUplift;
	
	private String fuelUpliftProcedureTitle;
	private String fuelUpliftProcedureReference;
	private String fuelUpliftProcedureDescription;
	private String fuelUpliftProcedurePost;
	private String fuelUpliftProcedureLocation;
	private String fuelUpliftProcedureSystem;
	
	private String fuelDensityFuelUpliftProcedureTitle;
	private String fuelDensityFuelUpliftProcedureReference;
	private String fuelDensityFuelUpliftProcedureDescription;
	private String fuelDensityFuelUpliftProcedurePost;
	private String fuelDensityFuelUpliftProcedureLocation;
	private String fuelDensityFuelUpliftProcedureSystem;
	
	private String blockOffBlockOnProcedureTitle;
	private String blockOffBlockOnProcedureReference;
	private String blockOffBlockOnProcedureDescription;
	private String blockOffBlockOnProcedurePost;
	private String blockOffBlockOnProcedureLocation;
	private String blockOffBlockOnProcedureSystem;
}
