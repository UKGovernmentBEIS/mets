package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurementn2o;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EtsEmissionPointCategoryRowMapper implements RowMapper<EtsN2OEmissionPointCategory> {

    @Override
    public EtsN2OEmissionPointCategory mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsN2OEmissionPointCategory.builder()
            .etsAccountId(rs.getString("fldEmitterID"))
            .sourceStreams(Stream.of(rs.getString("Mn2o_source_stream_refs").split("\\s*,\\s*"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet()))
            .emissionSources(Stream.of(rs.getString("Mn2o_emission_source_refs").split("\\s*,\\s*"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet()))
            .emissionPoint(rs.getString("Mn2o_emission_point_refs"))
            .estimatedEmission(rs.getString("Mn2o_estimated_annual_emissions"))
            .emissionPointCategory(rs.getString("Mn2o_source_stream_category"))
            .n2OEmissionsType(rs.getString("Mn2o_type_of_n2o_emissions"))
            .appliedApproach(rs.getString("Mn2o_applied_approach"))

            .measurementDevices(Stream.of(rs.getString("Mn2o_measurement_device_refs").split("\\s*,\\s*"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet()))
            .measurementFrequency(rs.getString("Mn2o_measurement_frequency"))
            .tierApplied(rs.getString("Mn2o_tier_applied"))
            .highestTierAppliedJustification(rs.getString("Mn2o_highest_tier_applied_justification"))

            .measurementParameter(rs.getString("Mn2o_parameter"))
            .measurementAppliedStandard(rs.getString("Mn2o_applied_standard"))
            .measurementDeviationsFromAppliedStandard(rs.getString("Mn2o_deviations_from_applied_standard"))
            .laboratoryName(rs.getString("Mn2o_laboratory_name"))
            .measurementLabIsoAccredited("yes".equalsIgnoreCase(rs.getString("Mn2o_lab_iso_accredited")))
            .measurementQualityAssuranceMeasures(rs.getString("Mn2o_quality_assurance_measures"))

            .build();
    }
}