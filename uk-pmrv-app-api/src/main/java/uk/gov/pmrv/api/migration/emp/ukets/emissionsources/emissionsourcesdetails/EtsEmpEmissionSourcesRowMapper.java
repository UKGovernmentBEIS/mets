package uk.gov.pmrv.api.migration.emp.ukets.emissionsources.emissionsourcesdetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EtsEmpEmissionSourcesRowMapper implements RowMapper<EtsEmpEmissionSources> {

	@Override
    public EtsEmpEmissionSources mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsEmpEmissionSources.builder()
        		.fldEmitterId(rs.getString("fldEmitterId"))
                .fldEmitterDisplayId(rs.getString("fldEmitterDisplayId"))
                .approachJustification(rs.getString("Approach_justification"))
                .procedureDetailsAdditionalAircraftTitle(rs.getString("Procedure_details_additional_aircraft_Procedure_title"))
                .procedureDetailsAdditionalAircraftReference(rs.getString("Procedure_details_additional_aircraft_Procedure_reference"))
                .procedureDetailsAdditionalAircraftDescription(rs.getString("Procedure_details_additional_aircraft_Procedure_description"))
                .procedureDetailsAdditionalAircraftPost(rs.getString("Procedure_details_additional_aircraft_Data_maintenance_post"))
                .procedureDetailsAdditionalAircraftLocation(rs.getString("Procedure_details_additional_aircraft_Records_location"))
                .procedureDetailsAdditionalAircraftSystem(rs.getString("Procedure_details_additional_aircraft_System_name"))
                .build();
    }
}
