package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurementco2;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.AppliedStandard;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Laboratory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MeasMonitoringApproachesEmissionPointCategoryMigrationService implements PermitSectionMigrationService<MeasurementOfCO2MonitoringApproach> {

    private final JdbcTemplate migrationJdbcTemplate;

    public MeasMonitoringApproachesEmissionPointCategoryMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
    }

    private static final String QUERY = """
        with emissions_emissionSource as (
            select fldEmitterID, me.emission_id, convert(varchar(256), trim(value)) emission_source
              from mig_permit_meas_emissions me cross apply string_split(Meas_emission_source_refs, ',')\s
             where trim(value) <> ''
        ), emissions_emissionPoint as (
            select fldEmitterID, me.emission_id, convert(varchar(256), trim(value)) emission_point
              from mig_permit_meas_emissions me cross apply string_split(Meas_emission_point_refs, ',')\s
             where trim(value) <> ''
        ), standards_emissionSource as (
            select s.fldEmitterID, standard_id, convert(varchar(256), trim(value)) emission_source
              from mig_permit_meas_standards s cross apply string_split(Meas_emission_source_refs, ',')\s
             where trim(value) <> ''
        ), standards_emissionPoint as (
            select s.fldEmitterID, standard_id, convert(varchar(256), trim(value)) emission_point
              from mig_permit_meas_standards s cross apply string_split(Meas_emission_point_refs, ',')\s
             where trim(value) <> ''
        )
        -- Match Measured emissions with Applied standards
        , t as (
            select distinct t.fldEmitterID, t.emission_id, tss.emission_source, tes.emission_point
              from mig_permit_meas_emissions t
              join emissions_emissionSource tss on tss.emission_id = t.emission_id
              join emissions_emissionPoint tes on tes.emission_id = t.emission_id
        ), tt as (
            select t.emission_id, count(*) cnt from t group by t.emission_id
        ), a as (
            select distinct a.fldEmitterID, a.standard_id, ass.emission_source, aes.emission_point
              from mig_permit_meas_standards a
              join standards_emissionSource ass on ass.standard_id = a.standard_id
              join standards_emissionPoint aes on aes.standard_id = a.standard_id
        ), aa as (
            select a.standard_id, count(*) cnt from a group by a.standard_id
        ), j as (
            select t.fldEmitterID t_emitter_id, t.emission_id, t.emission_source t_source_stream, t.emission_point t_emission_source,
                   a.standard_id, a.emission_source a_source_stream, a.emission_point a_emission_source
              from t
              left join a on a.fldEmitterID = t.fldEmitterID and a.emission_source = t.emission_source and a.emission_point = t.emission_point
        ), jj as (
            /* tiers with zero, one or many, full or partial combination match */
            select emission_id, standard_id, count(standard_id) matched_cnt
              from j
              group by emission_id, standard_id
        ), mx as (
            /* tiers that have one or many exact matches */
            select tt.emission_id, tt.cnt t_cnt, jj.standard_id, jj.matched_cnt, 'X' match_type
              from jj
              join tt on tt.emission_id = jj.emission_id and tt.cnt = jj.matched_cnt
              join aa on aa.standard_id = jj.standard_id and aa.cnt = jj.matched_cnt
        ), mp as (
            /* tiers that have one or many partial matches */
            select tt.emission_id, tt.cnt t_cnt, jj.standard_id, jj.matched_cnt, 'P' match_type
              from jj
              join tt on tt.emission_id = jj.emission_id and tt.cnt = jj.matched_cnt
        ), ma as (
            select mx.* from mx
             union all
            select mp.* from mp
              left join mx on mx.emission_id = mp.emission_id
             where mx.emission_id is null
        ), mm as (
            /* tiers that have only one full combination match [(srcStream, emSrc), parameter] */
            select emission_id, match_type, count(*) matches_found
              from ma
             group by emission_id, match_type
        ), results as (
            select mm.matches_found, mm.match_type, ma.standard_id, e.*
              from mig_permit_meas_emissions e
              left join mm on mm.emission_id = e.emission_id
              left join ma on ma.emission_id = mm.emission_id and mm.matches_found = 1
        ), md1 as (
            select emission_id, fldEmitterID, convert(varchar(256), trim(value)) meas_dev_ref
              from results cross apply string_split(Meas_measurement_device_refs, ',')
        ), md2 as (
            select distinct md1.emission_id, md1.fldEmitterID, d.Meas_measurement_frequency
              from md1 join mig_permit_meas_devices d on md1.fldEmitterID = d.fldEmitterID and md1.meas_dev_ref = d.Meas_measurement_device_ref
        )
        select r.fldEmitterID,
               -- Source stream category
               r.Meas_source_stream_refs,
               r.Meas_emission_source_refs,
               r.Meas_emission_point_refs,
               r.Meas_estimated_annual_emissions,
               r.Meas_source_stream_category,
              
               -- Measured emissions
               r.Meas_measurement_device_refs,
               (select md2.Meas_measurement_frequency from md2 where md2.fldEmitterID = r.fldEmitterID and md2.emission_id = r.emission_id) Meas_measurement_frequency,
               r.Meas_tier_applied,
               r.Meas_highest_tier_applied_justification,
              
               -- Applied standard
               s.Meas_parameter,
               s.Meas_applied_standard,
               s.Meas_deviations_from_applied_standard,
               s.Meas_laboratory_name,
               s.Meas_lab_iso_accredited,
               s.Meas_quality_assurance_measures
          from results r left join mig_permit_meas_standards s on s.standard_id = r.standard_id
        """;

    @Override
    public void populateSection(final Map<String, Account> accountsToMigratePermit,
                                final Map<Long, PermitMigrationContainer> permits) {

        final List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        final Map<String, MeasurementOfCO2MonitoringApproach> sections = this.queryEtsSection(accountIds);

        this.replaceReferencesWithIds(sections, permits, accountsToMigratePermit);

        sections.forEach((etsAccId, section) -> {
            final PermitMigrationContainer permitMigrationContainer =
                permits.get(accountsToMigratePermit.get(etsAccId).getId());

            final MeasurementOfCO2MonitoringApproach measurementMonitoringApproach =
                (MeasurementOfCO2MonitoringApproach) permitMigrationContainer
                    .getPermitContainer()
                    .getPermit()
                    .getMonitoringApproaches()
                    .getMonitoringApproaches()
                    .get(MonitoringApproachType.MEASUREMENT_CO2);
            measurementMonitoringApproach.setEmissionPointCategoryAppliedTiers(
                sections.get(etsAccId).getEmissionPointCategoryAppliedTiers()
            );
        });
    }

    @Override
    public Map<String, MeasurementOfCO2MonitoringApproach> queryEtsSection(final List<String> accountIds) {

        final String measurementEmissionPointCategoryQuery = this.getMeasurementEmissionPointCategoryQuery(accountIds);
        final Map<String, List<EtsMeasEmissionPointCategory>> allMeasurementEmissionPointCategories =
            this.executeEmissionPointCategoryQuery(measurementEmissionPointCategoryQuery, accountIds);

        final Map<String, MeasurementOfCO2MonitoringApproach> measurementMonitoringApproaches = new HashMap<>();

        allMeasurementEmissionPointCategories.forEach((etsAccountId, etsEmissionPointCategories) ->
            populateMeasurementMonitoringApproaches(measurementMonitoringApproaches, etsAccountId,
                etsEmissionPointCategories));
        return measurementMonitoringApproaches;
    }

    private void populateMeasurementMonitoringApproaches(
        Map<String, MeasurementOfCO2MonitoringApproach> measurementMonitoringApproaches, String etsAccountId,
        List<EtsMeasEmissionPointCategory> etsEmissionPointCategories) {
        final List<MeasurementOfCO2EmissionPointCategoryAppliedTier> appliedTiers = etsEmissionPointCategories.stream()
            .map(this::getAppliedTier)
            .collect(Collectors.toList());
        final MeasurementOfCO2MonitoringApproach monitoringApproach = MeasurementOfCO2MonitoringApproach.builder()
            .emissionPointCategoryAppliedTiers(appliedTiers)
            .build();
        measurementMonitoringApproaches.put(etsAccountId, monitoringApproach);
    }

    private MeasurementOfCO2EmissionPointCategoryAppliedTier getAppliedTier(EtsMeasEmissionPointCategory etsEmissionPointCategory) {
        MeasurementOfCO2EmissionPointCategory measEmissionPointCategory =
            determineMeasEmissionPointCategory(etsEmissionPointCategory);

        MeasurementOfCO2MeasuredEmissions measuredEmissions = determineMeasMeasuredEmissions(etsEmissionPointCategory);

        AppliedStandard appliedStandard = determineAppliedStandard(etsEmissionPointCategory);

        return MeasurementOfCO2EmissionPointCategoryAppliedTier.builder()
            .emissionPointCategory(measEmissionPointCategory)
            .measuredEmissions(measuredEmissions)
            .appliedStandard(appliedStandard)
            .build();
    }

    private static AppliedStandard determineAppliedStandard(EtsMeasEmissionPointCategory etsSourceStream) {
        return AppliedStandard.builder()
            .parameter(etsSourceStream.getMeasurementParameter())
            .appliedStandard(etsSourceStream.getMeasurementAppliedStandard())
            .deviationFromAppliedStandardExist(etsSourceStream.getMeasurementDeviationsFromAppliedStandard() != null)
            .deviationFromAppliedStandardDetails(etsSourceStream.getMeasurementDeviationsFromAppliedStandard())
            .laboratory(Laboratory.builder()
                .laboratoryName(etsSourceStream.getLaboratoryName())
                .laboratoryAccredited(etsSourceStream.isMeasurementLabIsoAccredited())
                .laboratoryAccreditationEvidence(etsSourceStream.isMeasurementLabIsoAccredited() ? null :
                    etsSourceStream.getMeasurementQualityAssuranceMeasures())
                .build())
            .build();
    }

    private MeasurementOfCO2MeasuredEmissions determineMeasMeasuredEmissions(EtsMeasEmissionPointCategory etsEmissionPointCategory) {
        MeasurementOfCO2MeasuredEmissionsTier measMeasuredEmissionsTier =
            StringToEnumConverter.measMeasuredEmissionTier(etsEmissionPointCategory.getTierApplied());
        return MeasurementOfCO2MeasuredEmissions.builder()
            .measurementDevicesOrMethods(etsEmissionPointCategory.getMeasurementDevices())
            .samplingFrequency(StringToEnumConverter.measuredSamplingFrequency(etsEmissionPointCategory.getMeasurementFrequency()))
            .tier(measMeasuredEmissionsTier)
            .highestRequiredTier(determineHighestRequiredTier(etsEmissionPointCategory, measMeasuredEmissionsTier))
            .build();
    }

    private HighestRequiredTier determineHighestRequiredTier(
        EtsMeasEmissionPointCategory etsSourceStream,
        MeasurementOfCO2MeasuredEmissionsTier measMeasuredEmissionsTier) {
        if (measMeasuredEmissionsTier == MeasurementOfCO2MeasuredEmissionsTier.TIER_4) {
            return null;
        }
        return HighestRequiredTier.builder()
            .isHighestRequiredTier(
                determineIfHighestRequiredTier(
                    measMeasuredEmissionsTier,
                    etsSourceStream.getHighestTierAppliedJustification()
                )
            )
            .noHighestRequiredTierJustification(
                determineNoHighestRequiredTierJustification(
                    measMeasuredEmissionsTier,
                    etsSourceStream.getHighestTierAppliedJustification()
                )
            )
            .build();
    }

    private static MeasurementOfCO2EmissionPointCategory determineMeasEmissionPointCategory(EtsMeasEmissionPointCategory etsEmissionPointCategory) {
        return MeasurementOfCO2EmissionPointCategory.builder()
            .emissionPoint(etsEmissionPointCategory.getEmissionPoint())
            .categoryType(StringToEnumConverter.categoryType(etsEmissionPointCategory.getEmissionPointCategory()))
            .annualEmittedCO2Tonnes(new BigDecimal(etsEmissionPointCategory.getEstimatedEmission()))
            .emissionSources(etsEmissionPointCategory.getEmissionSources())
            .sourceStreams(etsEmissionPointCategory.getSourceStreams())
            .build();
    }

    private NoHighestRequiredTierJustification determineNoHighestRequiredTierJustification(
        MeasurementOfCO2MeasuredEmissionsTier measMeasuredEmissionsTier,
        String highestTierAppliedJustification) {

        if (Objects.isNull(highestTierAppliedJustification) || measMeasuredEmissionsTier == MeasurementOfCO2MeasuredEmissionsTier.TIER_4) {
            return null;
        }
        return NoHighestRequiredTierJustification.builder()
            .isTechnicallyInfeasible(Boolean.TRUE)
            .technicalInfeasibilityExplanation(highestTierAppliedJustification)
            .build();
    }

    private Boolean determineIfHighestRequiredTier(
        MeasurementOfCO2MeasuredEmissionsTier measMeasuredEmissionTier,
        String highestTierAppliedJustification) {
        if (Objects.isNull(highestTierAppliedJustification) && measMeasuredEmissionTier != MeasurementOfCO2MeasuredEmissionsTier.TIER_4) {
            return Boolean.TRUE;
        } else if (Objects.nonNull(highestTierAppliedJustification) && measMeasuredEmissionTier != MeasurementOfCO2MeasuredEmissionsTier.TIER_4) {
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }

    private String getMeasurementEmissionPointCategoryQuery(final List<String> accountIds) {
        final StringBuilder measurementEmissionPointCategoryQueryBuilder = new StringBuilder(QUERY);
        if (!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            measurementEmissionPointCategoryQueryBuilder.append(String.format(" where r.fldEmitterID in (%s)",
                inAccountIdsSql));
        }
        return measurementEmissionPointCategoryQueryBuilder.toString();
    }

    private Map<String, List<EtsMeasEmissionPointCategory>> executeEmissionPointCategoryQuery(
        final String query,
        final List<String> accountIds) {
        final List<EtsMeasEmissionPointCategory> etsEmissionPointCategories =
            migrationJdbcTemplate.query(query,
                new EtsEmissionPointCategoryRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());

        return etsEmissionPointCategories
            .stream()
            .collect(Collectors.groupingBy(EtsMeasEmissionPointCategory::getEtsAccountId));
    }

    private void replaceReferencesWithIds(final Map<String, MeasurementOfCO2MonitoringApproach> sections,
                                          final Map<Long, PermitMigrationContainer> permits,
                                          final Map<String, Account> accountsToMigratePermit) {

        sections.forEach((id, approach) -> {

                final Long accountId = accountsToMigratePermit.get(id).getId();
                final Permit permit = permits.get(accountId).getPermitContainer().getPermit();
                final List<SourceStream> sourceStreams = permit.getSourceStreams().getSourceStreams();
                final List<EmissionSource> emissionSources = permit.getEmissionSources().getEmissionSources();
                final List<EmissionPoint> emissionPoints = permit.getEmissionPoints().getEmissionPoints();
                final List<MeasurementDeviceOrMethod> measurementDevicesOrMethodsIds =
                    permit.getMeasurementDevicesOrMethods().getMeasurementDevicesOrMethods();

                approach.getEmissionPointCategoryAppliedTiers().forEach(tier -> {
                        replaceEmissionPointReference(id, emissionPoints, tier);
                        replaceSourceStreamsReferences(id, sourceStreams, tier);
                        replaceEmissionSourcesReferences(id, emissionSources, tier);
                        replaceMeasurementDevicesOrMethodsReferences(id, measurementDevicesOrMethodsIds, tier);
                    }
                );
            }
        );
    }

    private static void replaceMeasurementDevicesOrMethodsReferences(
        String id,
        List<MeasurementDeviceOrMethod> measurementDevicesOrMethodsIds,
        MeasurementOfCO2EmissionPointCategoryAppliedTier tier) {
        final Set<String> measurementDeviceOrMethodsIds =
            tier.getMeasuredEmissions().getMeasurementDevicesOrMethods().stream().map(
                measurementDeviceRef -> measurementDevicesOrMethodsIds.stream()
                    .filter(device -> device.getReference().trim().equalsIgnoreCase(measurementDeviceRef.trim()))
                    .findAny()
                    .map(MeasurementDeviceOrMethod::getId)
                    .orElseGet(() -> {
                        log.error(String.format("cannot find measurement devices or methods with reference %s for " +
                            "emitter" +
                            " %s", measurementDeviceRef, id));
                        return measurementDeviceRef;
                    })
            ).collect(Collectors.toSet());
        tier.getMeasuredEmissions().setMeasurementDevicesOrMethods(measurementDeviceOrMethodsIds);
    }

    private static void replaceEmissionSourcesReferences(String id, List<EmissionSource> emissionSources,
                                                         MeasurementOfCO2EmissionPointCategoryAppliedTier tier) {
        final Set<String> emissionSourcesIds =
            tier.getEmissionPointCategory().getEmissionSources().stream().map(
                emissionSourceRef -> emissionSources.stream()
                    .filter(es -> es.getReference().trim().equalsIgnoreCase(emissionSourceRef.trim()))
                    .findAny()
                    .map(EmissionSource::getId)
                    .orElseGet(() -> {
                        log.error(String.format("cannot find emission source with reference %s for " +
                                "emitter %s",
                            emissionSourceRef,
                            id));
                        return emissionSourceRef;
                    })
            ).collect(Collectors.toSet());
        tier.getEmissionPointCategory().setEmissionSources(emissionSourcesIds);
    }

    private static void replaceSourceStreamsReferences(String id, List<SourceStream> sourceStreams,
                                                       MeasurementOfCO2EmissionPointCategoryAppliedTier tier) {
        final Set<String> sourceStreamIds =
            tier.getEmissionPointCategory().getSourceStreams().stream().map(
                sourceStreamRef -> sourceStreams.stream()
                    .filter(ss -> ss.getReference().trim().equalsIgnoreCase(sourceStreamRef.trim()))
                    .findAny()
                    .map(SourceStream::getId)
                    .orElseGet(() -> {
                        log.error(String.format("cannot find source stream with reference %s for " +
                            "emitter %s", sourceStreamRef, id));
                        return sourceStreamRef;
                    })
            ).collect(Collectors.toSet());

        tier.getEmissionPointCategory().setSourceStreams(sourceStreamIds);
    }

    private static void replaceEmissionPointReference(String id, List<EmissionPoint> emissionPoints,
                                                      MeasurementOfCO2EmissionPointCategoryAppliedTier tier) {
        final String emissionPointRef = tier.getEmissionPointCategory().getEmissionPoint();
        final String emissionPointId = emissionPoints.stream()
            .filter(ep -> ep.getReference().trim().equalsIgnoreCase(emissionPointRef.trim()))
            .findAny()
            .map(EmissionPoint::getId)
            .orElseGet(() -> {
                log.error(String.format("cannot find emission point with reference %s for emitter %s"
                    , emissionPointRef, id));
                return emissionPointRef;
            });
        tier.getEmissionPointCategory().setEmissionPoint(emissionPointId);
    }
}
