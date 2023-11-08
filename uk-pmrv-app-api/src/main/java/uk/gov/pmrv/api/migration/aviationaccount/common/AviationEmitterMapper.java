package uk.gov.pmrv.api.migration.aviationaccount.common;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class AviationEmitterMapper implements RowMapper<AviationEmitter>{

	@Override
    public AviationEmitter mapRow(ResultSet rs, int i) throws SQLException {
        return AviationEmitter.builder()
                .ca(rs.getString("ca"))
                .scope(rs.getString("scope"))
                .fldEmitterId(rs.getString("fldEmitterID"))
                .emitterDisplayId(rs.getString("emitterDisplayId"))  
                .emitterStatus(rs.getString("emitterStatus"))
                .fldName(rs.getString("fldName"))
                .fldNapBenchmarkAllowances(rs.getObject("fldNapBenchmarkAllowances") != null ? rs.getLong("fldNapBenchmarkAllowances") : null)
                .fldRegistration((rs.getObject("fldRegistration") != null && !"-".equals(rs.getObject("fldRegistration"))) ? rs.getInt("fldRegistration") : null)
                .fldCrcoCode(rs.getString("fldCrcoCode"))
                .fldFirstFlyDate(rs.getTimestamp("fldFirstFlyDate") != null ? rs.getTimestamp("fldFirstFlyDate").toLocalDateTime().toLocalDate() : null)
                .fldDateCreated(rs.getTimestamp("fldDateCreated") != null ? rs.getTimestamp("fldDateCreated").toLocalDateTime() : null)
                .createdBy(rs.getString("createdBy"))
                .addressLine1(rs.getString("address_line1"))
                .addressLine2(rs.getString("address_line2"))
                .city(rs.getString("city"))
                .stateProvinceRegion(rs.getString("state_province_region"))
                .postCodeZip(rs.getString("postcode_zip"))
                .country(rs.getString("country"))
                .reportingStatus(rs.getString("reportingStatus"))
                .reportingStatusReason(rs.getString("reportingStatusReason"))
                .reportingStatusSubmissionDate(rs.getTimestamp("reportingStatusSubmissionDate") != null ? rs.getTimestamp("reportingStatusSubmissionDate").toLocalDateTime() : null)
                .reportingStatusSubmittedBy(rs.getString("reportingStatusSubmittedBy")) 
                .vbId(rs.getString("vbId"))
                .vbName(rs.getString("vbName"))
                .build();
    }
}
