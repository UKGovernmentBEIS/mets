package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurementco2;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EtsEmissionPointCategoryRowMapper implements RowMapper<EtsMeasEmissionPointCategory> {

    @Override
    public EtsMeasEmissionPointCategory mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsMeasEmissionPointCategory.builder()
            .etsAccountId(rs.getString("fldEmitterID"))
            .sourceStreams(Stream.of(rs.getString("Meas_source_stream_refs").split("\\s*,\\s*"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet()))
            .emissionSources(Stream.of(rs.getString("Meas_emission_source_refs").split("\\s*,\\s*"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet()))
            .emissionPoint(rs.getString("Meas_emission_point_refs"))
            .estimatedEmission(rs.getString("Meas_estimated_annual_emissions"))
            .emissionPointCategory(rs.getString("Meas_source_stream_category"))

            .measurementDevices(Stream.of(rs.getString("Meas_measurement_device_refs").split("\\s*,\\s*"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet()))
            .measurementFrequency(rs.getString("Meas_measurement_frequency"))
            .tierApplied(rs.getString("Meas_tier_applied"))
            .highestTierAppliedJustification(rs.getString("Meas_highest_tier_applied_justification"))

            .measurementParameter(rs.getString("Meas_parameter"))
            .measurementAppliedStandard(rs.getString("Meas_applied_standard"))
            .measurementDeviationsFromAppliedStandard(rs.getString("Meas_deviations_from_applied_standard"))
            .laboratoryName(rs.getString("Meas_laboratory_name"))
            .measurementLabIsoAccredited("yes".equalsIgnoreCase(rs.getString("Meas_lab_iso_accredited")))
            .measurementQualityAssuranceMeasures(rs.getString("Meas_quality_assurance_measures"))

            .build();
    }
}