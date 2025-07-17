package uk.gov.pmrv.api.migration.emp.corsia.monitoringapproach;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsMonitoringApproachCorsiaRowMapper implements RowMapper<EtsMonitoringApproachCorsia> {

	@Override
    public EtsMonitoringApproachCorsia mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        return EtsMonitoringApproachCorsia.builder()
                .fldEmitterID(resultSet.getString("fldEmitterID"))
                .fldEmitterDisplayID(resultSet.getString("fldEmitterDisplayID"))
                .empVersion(resultSet.getInt("empVersion"))
                .corsiaMonitoringMethod(resultSet.getString("Corsia_monitoring_method"))
                .corsiaCertType(resultSet.getString("Corsia_cert_type"))
                .icaoCertUsedDesc(resultSet.getString("Icao_cert_used_desc"))
                .evidenceStoredFileName(resultSet.getString("evidence_storedFileName"))
                .evidenceUploadedFileName(resultSet.getString("evidence_uploadedFileName"))
                .build();
    }
}
