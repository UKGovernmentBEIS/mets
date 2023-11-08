package uk.gov.pmrv.api.migration.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import org.springframework.jdbc.core.RowMapper;
import uk.gov.pmrv.api.reporting.domain.verification.OverallAssessmentType;

public class ReportMapper implements RowMapper<Report> {

    @Override
    public Report mapRow(ResultSet rs, int i) throws SQLException {

        return Report.builder()
            .accountId(rs.getLong("fldEmitterDisplayID"))
            .reportingYear(rs.getInt("reporting_year") != 0 ? Year.of(rs.getInt("reporting_year")) : null)
            .aerId(rs.getString("wf_aer_identifier"))
            .aerCreatedDate(rs.getDate("wf_aer_date_created") != null ? 
                rs.getDate("wf_aer_date_created").toLocalDate() : null)
            .dreId(rs.getString("wf_dre_identifier"))
            .dreCreatedDate(rs.getDate("wf_dre_date_created") != null ?
                    rs.getDate("wf_dre_date_created").toLocalDate() : null)
            .overallAssessmentType(this.mapOverallAssessmentType(rs.getString("Vos_opinion")))
            .aerApprovedReportableEmissions(
                rs.getString("wf_aer_approved_reportable_emissions") != null ?
                    BigDecimal.valueOf(Double.parseDouble(rs.getString("wf_aer_approved_reportable_emissions"))) :
                    null
            )
            .reportType(rs.getString("reportType"))
            .build();
    }

    private OverallAssessmentType mapOverallAssessmentType(final String overallAssessmentType) {

        if ("not verified".equalsIgnoreCase(overallAssessmentType)) {
            return OverallAssessmentType.NOT_VERIFIED;
        } else if ("verified".equalsIgnoreCase(overallAssessmentType)) {
            return OverallAssessmentType.VERIFIED_AS_SATISFACTORY;
        } else if ("verified with comments".equalsIgnoreCase((overallAssessmentType))) {
            return OverallAssessmentType.VERIFIED_WITH_COMMENTS;
        }
        return null;
    }
}
