package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationco2;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsCalculationAnalysisMethodRowMapper implements RowMapper<EtsCalculationAnalysisMethod> {

    @Override
    public EtsCalculationAnalysisMethod mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsCalculationAnalysisMethod.builder()

            .tierId(rs.getString("tier_id"))
            .etsAccountId(rs.getString("fldEmitterID"))
            .parameter(rs.getString("parameter"))
            .analysisMethod(rs.getString("method_of_analysis"))
            .otherFrequency(rs.getBoolean("frequency_other"))
            .frequency(rs.getString("frequency"))
            .frequencyMeetsMinRequirements("yes".equalsIgnoreCase(rs.getString("frequency_meet_mrr")))
            .reducedFrequencyReason(rs.getString("frequency_less_mrr_reason"))
            .laboratoryName(rs.getString("laboratory_name"))
            .laboratoryAccredited("yes".equalsIgnoreCase(rs.getString("is_lab_iso_accredited")))
            .build();
    }
}