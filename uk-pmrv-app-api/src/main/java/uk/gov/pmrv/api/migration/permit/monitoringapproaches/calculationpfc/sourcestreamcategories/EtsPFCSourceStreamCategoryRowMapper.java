package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationpfc.sourcestreamcategories;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EtsPFCSourceStreamCategoryRowMapper implements RowMapper<EtsPFCSourceStreamCategory> {
    @Override
    public EtsPFCSourceStreamCategory mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsPFCSourceStreamCategory.builder()
                .etsAccountId(rs.getString("fldEmitterID"))

                .sourceStream(rs.getString("Mpfc_source_stream_refs"))
                .emissionSources(Stream.of(rs.getString("Mpfc_emission_source_refs").split("\\s*,\\s*"))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet()))
                .estimatedEmission(rs.getString("Mpfc_estimated_annual_emissions"))
                .sourceStreamCategory(rs.getString("Mpfc_source_stream_category"))
                .emissionPoints(Stream.of(rs.getString("Mpfc_emission_point_refs").split("\\s*,\\s*"))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet()))
                .calculationMethod(rs.getString("Mpfc_calculation_method_applied"))

                .massBalanceApproachUsed("yes".equalsIgnoreCase(rs.getString("Mpfc_is_mass_balance_approach")))
                .activityDataTierApplied(rs.getString("Mpfc_activity_data_tier_applied"))
                .activityDataIsMiddleTier(rs.getBoolean("ad_is_middle_tier"))
                .tierJustification(rs.getString("Mpfc_highest_tiers_justification"))

                .emissionFactorTierApplied(rs.getString("Mpfc_emission_factor_tier_applied"))
                .emissionFactorIsMiddleTier(rs.getBoolean("ef_is_middle_tier"))
                .build();
    }
}
