package uk.gov.pmrv.api.migration.emp.corsia.emissionsources;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsEmissionSourcesCorsiaRowMapper implements RowMapper<EtsEmissionSourcesCorsia> {

	@Override
    public EtsEmissionSourcesCorsia mapRow(ResultSet rs, int i) throws SQLException {
        return EtsEmissionSourcesCorsia.builder()
                .fldEmitterID(rs.getString("fldEmitterID"))
                .fldEmitterDisplayId(rs.getString("fldEmitterDisplayID"))  
                .genericAircraftType(rs.getString("Generic_aircraft_type"))
                .hMake(rs.getString("Hmake"))
                .hModel(rs.getString("Hmodel"))
                .hDesignator(rs.getString("Hdesignator"))
                .genericAircraftSubtype(rs.getString("Generic_aircraft_subtype"))
                .numberOfAircraft((rs.getObject("Number_of_aircraft") != null) ? rs.getLong("Number_of_aircraft") : null)
                .chkJetKerosene(Boolean.valueOf(rs.getString("Chk_jet_kerosene")))
                .chkJetGasoline(Boolean.valueOf(rs.getString("Chk_jet_gasoline")))
                .chkAviationGasoline(Boolean.valueOf(rs.getString("Chk_aviation_gasoline")))
                .corsiaMethodology(rs.getString("Corsia_methodology"))
                .build();
    }
}
