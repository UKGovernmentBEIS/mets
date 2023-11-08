package uk.gov.pmrv.api.migration.emp.ukets.emissionsources.aircrafttypedetails;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsAircraftTypeDetailsRowMapper implements RowMapper<EtsAircraftTypeDetails>{

	@Override
    public EtsAircraftTypeDetails mapRow(ResultSet rs, int i) throws SQLException {
        return EtsAircraftTypeDetails.builder()
                .fldEmitterId(rs.getString("fldEmitterID"))
                .fldEmitterDisplayId(rs.getString("fldEmitterDisplayId"))  
                .genericAircraftType(rs.getString("Generic_aircraft_type"))
                .hMake(rs.getString("Hmake"))
                .hModel(rs.getString("Hmodel"))
                .hDesignator(rs.getString("Hdesignator"))
                .genericAircraftSubtype(rs.getString("Generic_aircraft_subtype"))
                .numberOfAircraft((rs.getObject("Number_of_aircraft") != null) ? rs.getLong("Number_of_aircraft") : null)
                .chkJetKerosene(Boolean.valueOf(rs.getString("Chk_jet_kerosene")))
                .chkJetGasoline(Boolean.valueOf(rs.getString("Chk_jet_gasoline")))
                .chkAviationGasoline(Boolean.valueOf(rs.getString("Chk_aviation_gasoline")))
                .methodology(rs.getString("Methodology"))
                .build();
    }
}
