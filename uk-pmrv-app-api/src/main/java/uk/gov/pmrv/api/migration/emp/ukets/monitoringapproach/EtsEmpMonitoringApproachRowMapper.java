package uk.gov.pmrv.api.migration.emp.ukets.monitoringapproach;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsEmpMonitoringApproachRowMapper implements RowMapper<EtsEmpMonitoringApproach> {

    @Override
    public EtsEmpMonitoringApproach mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        return EtsEmpMonitoringApproach.builder()
                .etsAccountId(resultSet.getString("fldEmitterID"))
                .isSimplifiedProcedure(resultSet.getBoolean("Simplified_procedures_yes"))
                .monitoringApproach(resultSet.getString("Tools_used"))
                .simplifiedReportingEligibility(resultSet.getString("Eligibility_information_simplified_calculation_proce"))
                .storedFileName(resultSet.getString("Eligibility_documents_storedFileName"))
                .uploadedFileName(resultSet.getString("Eligibility_documents_uploadedFileName"))
                .build();
    }
}
