package uk.gov.pmrv.api.migration.report.aviation;

import org.springframework.jdbc.core.RowMapper;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerVerificationDecisionType;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

public class AviationReportMapper implements RowMapper<AviationReport> {

    @Override
    public AviationReport mapRow(ResultSet rs, int i) throws SQLException {

        String scope = rs.getString("scope");
        String opinion;
        if (AviationAccountType.UKETS.toString().equals(scope)) {
            opinion = rs.getString("Vos_opinion");
        } else if (AviationAccountType.CORSIA.toString().equals(scope)) {
            opinion = rs.getString("Vcos_opinion");
        } else {
            opinion = null;
        }

        return AviationReport.builder()
            .accountType(AviationAccountType.valueOf(scope))
            .accountId(rs.getLong("fldEmitterDisplayID"))
            .reportingYear(rs.getInt("reporting_year") != 0 ? Year.of(rs.getInt("reporting_year")) : null)
            .aerId(rs.getString("wf_aer_identifier"))
            .aerStatus(rs.getString("wf_aer_status"))
            .aerCreatedDate(rs.getDate("wf_aer_date_created") != null ?
                rs.getDate("wf_aer_date_created").toLocalDate() : null)
            .overallAssessmentType(this.mapAerVerificationDecisionType(opinion))
            .aerApprovedReportableEmissions(
                rs.getString("wf_aer_approved_reportable_emissions") != null ?
                    BigDecimal.valueOf(Double.parseDouble(rs.getString("wf_aer_approved_reportable_emissions"))) :
                    null
            )
            .dreId(rs.getString("wf_dre_identifier"))
            .dreCreatedDate(rs.getDate("wf_dre_date_created") != null ?
                rs.getDate("wf_dre_date_created").toLocalDate() : null)
            .totalEmissionsInternationalFlight(rs.getString("total_emissions_international_flight") != null ?
                BigDecimal.valueOf(Double.parseDouble(rs.getString("total_emissions_international_flight"))) :
                null
            )
            .totalEmissionsOffsettingFlights(rs.getString("total_emissions_offsetting_flights") != null ?
                BigDecimal.valueOf(Double.parseDouble(rs.getString("total_emissions_offsetting_flights"))) :
                null
            )
            .totalEmissionsClaimedReductions(rs.getString("total_emissions_claimed_reductions") != null ?
                BigDecimal.valueOf(Double.parseDouble(rs.getString("total_emissions_claimed_reductions"))) :
                null
            )
            .build();
    }

    private AviationAerVerificationDecisionType mapAerVerificationDecisionType(final String overallAssessmentType) {

        if ("not verified".equalsIgnoreCase(overallAssessmentType)) {
            return AviationAerVerificationDecisionType.NOT_VERIFIED;
        } else if ("verified".equalsIgnoreCase(overallAssessmentType)) {
            return AviationAerVerificationDecisionType.VERIFIED_AS_SATISFACTORY;
        } else if ("verified with comments".equalsIgnoreCase(overallAssessmentType)) {
            return AviationAerVerificationDecisionType.VERIFIED_AS_SATISFACTORY_WITH_COMMENTS;
        }
        return null;
    }
}