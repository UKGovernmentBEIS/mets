package uk.gov.pmrv.api.migration.permit.monitoringapproaches.fallback;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EtsFallbackSourceStreamCategoryRowMapper implements RowMapper<EtsFallbackSourceStreamCategory> {


    @Override
    public EtsFallbackSourceStreamCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsFallbackSourceStreamCategory.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .sourceStream(rs.getString("Calc_tiers_source_stream_refs"))
                .emissionSources(Stream.of(rs.getString("Calc_tiers_emission_source_refs").split("\\s*,\\s*"))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet()))
                .estimatedEmission(rs.getString("Calc_tiers_estimated_emission"))
                .sourceStreamCategory(rs.getString("Calc_tiers_source_category"))
                .meteringUncertainty(rs.getString("Calc_tiers_overall_metering_uncertainty"))
                .measurementDevices(Stream.of(rs.getString("Calc_tiers_measurement_device_refs").split("\\s*,\\s*"))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet()))
                .build();
    }
}
