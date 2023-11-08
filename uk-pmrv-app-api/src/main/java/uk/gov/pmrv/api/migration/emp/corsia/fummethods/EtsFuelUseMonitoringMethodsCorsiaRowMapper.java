package uk.gov.pmrv.api.migration.emp.corsia.fummethods;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EtsFuelUseMonitoringMethodsCorsiaRowMapper implements RowMapper<EtsFuelUseMonitoringMethodsCorsia> {

	@Override
    public EtsFuelUseMonitoringMethodsCorsia mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        return EtsFuelUseMonitoringMethodsCorsia.builder()
                .fldEmitterID(resultSet.getString("fldEmitterID"))
                .fldEmitterDisplayID(resultSet.getString("fldEmitterDisplayID"))
                .corsiaApproachJustification(resultSet.getString("Corsia_approach_justification"))
                
                .methodAProcedureTitle(resultSet.getString("Procedure_details_methoda_Procedure_title"))
                .methodAProcedureReference(resultSet.getString("Procedure_details_methoda_Procedure_reference"))
                .methodAProcedureDescription(resultSet.getString("Procedure_details_methoda_Procedure_description"))
                .methodAProcedurePost(resultSet.getString("Procedure_details_methoda_Data_maintenance_post"))
                .methodAProcedureLocation(resultSet.getString("Procedure_details_methoda_Records_location"))
                .methodAProcedureSystem(resultSet.getString("Procedure_details_methoda_System_name"))
                .fuelDensityMethodAProcedureTitle(resultSet.getString("Fuel_density_methoda_Procedure_title"))
                .fuelDensityMethodAProcedureReference(resultSet.getString("Fuel_density_methoda_Procedure_reference"))
                .fuelDensityMethodAProcedureDescription(resultSet.getString("Fuel_density_methoda_Procedure_description"))
                .fuelDensityMethodAProcedurePost(resultSet.getString("Fuel_density_methoda_Data_maintenance_post"))
                .fuelDensityMethodAProcedureLocation(resultSet.getString("Fuel_density_methoda_Records_location"))
                .fuelDensityMethodAProcedureSystem(resultSet.getString("Fuel_density_methoda_System_name"))
                
                .methodBProcedureTitle(resultSet.getString("Procedure_details_methodb_Procedure_title"))
                .methodBProcedureReference(resultSet.getString("Procedure_details_methodb_Procedure_reference"))
                .methodBProcedureDescription(resultSet.getString("Procedure_details_methodb_Procedure_description"))
                .methodBProcedurePost(resultSet.getString("Procedure_details_methodb_Data_maintenance_post"))
                .methodBProcedureLocation(resultSet.getString("Procedure_details_methodb_Records_location"))
                .methodBProcedureSystem(resultSet.getString("Procedure_details_methodb_System_name"))
                .fuelDensityMethodBProcedureTitle(resultSet.getString("Fuel_density_methodb_Procedure_title"))
                .fuelDensityMethodBProcedureReference(resultSet.getString("Fuel_density_methodb_Procedure_reference"))
                .fuelDensityMethodBProcedureDescription(resultSet.getString("Fuel_density_methodb_Procedure_description"))
                .fuelDensityMethodBProcedurePost(resultSet.getString("Fuel_density_methodb_Data_maintenance_post"))
                .fuelDensityMethodBProcedureLocation(resultSet.getString("Fuel_density_methodb_Records_location"))
                .fuelDensityMethodBProcedureSystem(resultSet.getString("Fuel_density_methodb_System_name"))
                
                .zeroFuelUpliftDescription(resultSet.getString("Zero_fuel_uplift_description"))
                .fuelUplift(resultSet.getString("Fuel_uplift"))
                .fuelUpliftProcedureTitle(resultSet.getString("Procedure_details_fuel_uplift_Procedure_title"))
                .fuelUpliftProcedureReference(resultSet.getString("Procedure_details_fuel_uplift_Procedure_reference"))
                .fuelUpliftProcedureDescription(resultSet.getString("Procedure_details_fuel_uplift_Procedure_description"))
                .fuelUpliftProcedurePost(resultSet.getString("Procedure_details_fuel_uplift_Data_maintenance_post"))
                .fuelUpliftProcedureLocation(resultSet.getString("Procedure_details_fuel_uplift_Records_location"))
                .fuelUpliftProcedureSystem(resultSet.getString("Procedure_details_fuel_uplift_System_name"))
                .fuelDensityFuelUpliftProcedureTitle(resultSet.getString("Fuel_density_fuel_uplift_Procedure_title"))
                .fuelDensityFuelUpliftProcedureReference(resultSet.getString("Fuel_density_fuel_uplift_Procedure_reference"))
                .fuelDensityFuelUpliftProcedureDescription(resultSet.getString("Fuel_density_fuel_uplift_Procedure_description"))
                .fuelDensityFuelUpliftProcedurePost(resultSet.getString("Fuel_density_fuel_uplift_Data_maintenance_post"))
                .fuelDensityFuelUpliftProcedureLocation(resultSet.getString("Fuel_density_fuel_uplift_Records_location"))
                .fuelDensityFuelUpliftProcedureSystem(resultSet.getString("Fuel_density_fuel_uplift_System_name"))
                
                .blockOffBlockOnProcedureTitle(resultSet.getString("Procedure_details_block_off_block_on_Procedure_title"))
                .blockOffBlockOnProcedureReference(resultSet.getString("Procedure_details_block_off_block_on_Procedure_reference"))
                .blockOffBlockOnProcedureDescription(resultSet.getString("Procedure_details_block_off_block_on_Procedure_description"))
                .blockOffBlockOnProcedurePost(resultSet.getString("Procedure_details_block_off_block_on_Data_maintenance_post"))
                .blockOffBlockOnProcedureLocation(resultSet.getString("Procedure_details_block_off_block_on_Records_location"))
                .blockOffBlockOnProcedureSystem(resultSet.getString("Procedure_details_block_off_block_on_System_name"))
                .build();
    }
}
