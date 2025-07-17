package uk.gov.pmrv.api.migration.emp.ukets.fummethods;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsFuelUseMonitoringMethodsRowMapper implements RowMapper<EtsFuelUseMonitoringMethods> {

	@Override
    public EtsFuelUseMonitoringMethods mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsFuelUseMonitoringMethods.builder()
        		.fldEmitterId(rs.getString("fldEmitterId"))
                .fldEmitterDisplayId(rs.getString("fldEmitterDisplayId"))
                .procedureDetailsTitle(rs.getString("Procedure_details_Procedure_title"))
                .procedureDetailsReference(rs.getString("Procedure_details_Procedure_reference"))
                .procedureDetailsDescription(rs.getString("Procedure_details_Procedure_description"))
                .procedureDetailsPost(rs.getString("Procedure_details_Data_maintenance_post"))
                .procedureDetailsLocation(rs.getString("Procedure_details_Records_location"))
                .procedureDetailsSystem(rs.getString("Procedure_details_System_name"))
                .densityMeasurementProcedureTitle(rs.getString("Density_measurement_procedure_Procedure_title"))
                .densityMeasurementProcedureReference(rs.getString("Density_measurement_procedure_Procedure_reference"))
                .densityMeasurementProcedureDescription(rs.getString("Density_measurement_procedure_Procedure_description"))
                .densityMeasurementProcedurePost(rs.getString("Density_measurement_procedure_Data_maintenance_post"))
                .densityMeasurementProcedureLocation(rs.getString("Density_measurement_procedure_Records_location"))
                .densityMeasurementProcedureSystem(rs.getString("Density_measurement_procedure_System_name"))
                .build();
    }
}
