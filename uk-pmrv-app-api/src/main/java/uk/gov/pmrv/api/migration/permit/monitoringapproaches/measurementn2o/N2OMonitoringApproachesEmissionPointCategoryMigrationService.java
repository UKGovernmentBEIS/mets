package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurementn2o;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
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
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
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
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class N2OMonitoringApproachesEmissionPointCategoryMigrationService implements PermitSectionMigrationService<MeasurementOfN2OMonitoringApproach> {

    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY = """
        with emissions_emissionSource as (
            select fldEmitterID, me.emission_id, convert(varchar(256), trim(value)) emission_source
              from mig_permit_n2o_emissions me cross apply string_split(Mn2o_emission_source_refs, ',')
             where trim(value) <> ''
        ), emissions_emissionPoint as (
            select fldEmitterID, me.emission_id, convert(varchar(256), trim(value)) emission_point
              from mig_permit_n2o_emissions me cross apply string_split(Mn2o_emission_point_refs, ',')
             where trim(value) <> ''
        ), standards_emissionSource as (
            select s.fldEmitterID, standard_id, convert(varchar(256), trim(value)) emission_source
              from mig_permit_n2o_standards s cross apply string_split(Mn2o_emission_source_refs, ',')
             where trim(value) <> ''
        ), standards_emissionPoint as (
            select s.fldEmitterID, standard_id, convert(varchar(256), trim(value)) emission_point
              from mig_permit_n2o_standards s cross apply string_split(Mn2o_emission_point_refs, ',')
             where trim(value) <> ''
        )
        -- Match Measured emissions with Applied standards
        , t as (
            select distinct t.fldEmitterID, t.emission_id, tss.emission_source, tes.emission_point
              from mig_permit_n2o_emissions t
              join emissions_emissionSource tss on tss.emission_id = t.emission_id
              join emissions_emissionPoint tes on tes.emission_id = t.emission_id
        ), tt as (
            select t.emission_id, count(*) cnt from t group by t.emission_id
        ), a as (
            select distinct a.fldEmitterID, a.standard_id, ass.emission_source, aes.emission_point
              from mig_permit_n2o_standards a
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
              from mig_permit_n2o_emissions e
              left join mm on mm.emission_id = e.emission_id
              left join ma on ma.emission_id = mm.emission_id and mm.matches_found = 1
        ), md1 as (
            select emission_id, fldEmitterID, convert(varchar(256), trim(value)) meas_dev_ref
              from results cross apply string_split(Mn2o_measurement_device_refs, ',')
        ), md2 as (
            select distinct md1.emission_id, md1.fldEmitterID, d.Mn2o_measurement_frequency
              from md1 join mig_permit_n2o_devices d on md1.fldEmitterID = d.fldEmitterID and md1.meas_dev_ref = d.Mn2o_measurement_device_ref
        )
        select r.fldEmitterID,
               -- Source stream category
               r.Mn2o_source_stream_refs,
               r.Mn2o_emission_point_refs,
               r.Mn2o_emission_source_refs,
               r.Mn2o_type_of_n2o_emissions,
               r.Mn2o_applied_approach,
               r.Mn2o_estimated_annual_emissions,
               r.Mn2o_source_stream_category,
              
               -- Measured emissions
               r.Mn2o_measurement_device_refs,
               (select md2.Mn2o_measurement_frequency from md2 where md2.fldEmitterID = r.fldEmitterID and md2.emission_id = r.emission_id) Mn2o_measurement_frequency,
               r.Mn2o_tier_applied,
               r.Mn2o_highest_tier_applied_justification,
              
               -- Applied standard
               s.Mn2o_parameter,
               s.Mn2o_applied_standard,
               s.Mn2o_deviations_from_applied_standard,
               s.Mn2o_laboratory_name,
               s.Mn2o_lab_iso_accredited,
               s.Mn2o_quality_assurance_measures
          from results r left join mig_permit_n2o_standards s on s.standard_id = r.standard_id
        """;

    @Override
    public void populateSection(final Map<String, Account> accountsToMigratePermit,
                                final Map<Long, PermitMigrationContainer> permits) {

        final List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        final Map<String, MeasurementOfN2OMonitoringApproach> sections = this.queryEtsSection(accountIds);

        this.replaceReferencesWithIds(sections, permits, accountsToMigratePermit);

        sections.forEach((etsAccId, section) -> {
            final PermitMigrationContainer permitMigrationContainer =
                permits.get(accountsToMigratePermit.get(etsAccId).getId());

            final MeasurementOfN2OMonitoringApproach n2OMonitoringApproach =
                (MeasurementOfN2OMonitoringApproach) permitMigrationContainer
                    .getPermitContainer()
                    .getPermit()
                    .getMonitoringApproaches()
                    .getMonitoringApproaches()
                    .get(MonitoringApproachType.MEASUREMENT_N2O);
            n2OMonitoringApproach.setEmissionPointCategoryAppliedTiers(
                sections.get(etsAccId).getEmissionPointCategoryAppliedTiers()
            );
        });
    }

    @Override
    public Map<String, MeasurementOfN2OMonitoringApproach> queryEtsSection(final List<String> accountIds) {

        final String n2OEmissionPointCategoryQuery = this.getN2OEmissionPointCategoryQuery(accountIds);
        final Map<String, List<EtsN2OEmissionPointCategory>> allN2OEmissionPointCategories =
            this.executeEmissionPointCategoryQuery(n2OEmissionPointCategoryQuery, accountIds);

        final Map<String, MeasurementOfN2OMonitoringApproach> n2OMonitoringApproaches = new HashMap<>();

        allN2OEmissionPointCategories.forEach((etsAccountId, etsEmissionPointCategories) ->
            populateN2OMonitoringApproaches(n2OMonitoringApproaches, etsAccountId,
                etsEmissionPointCategories));
        return n2OMonitoringApproaches;
    }

    private void populateN2OMonitoringApproaches(
        Map<String, MeasurementOfN2OMonitoringApproach> n2OMonitoringApproaches, String etsAccountId,
        List<EtsN2OEmissionPointCategory> etsEmissionPointCategories) {
        final List<MeasurementOfN2OEmissionPointCategoryAppliedTier> appliedTiers = etsEmissionPointCategories.stream()
            .map(this::getAppliedTier)
            .collect(Collectors.toList());
        final MeasurementOfN2OMonitoringApproach monitoringApproach = MeasurementOfN2OMonitoringApproach.builder()
            .emissionPointCategoryAppliedTiers(appliedTiers)
            .build();
        n2OMonitoringApproaches.put(etsAccountId, monitoringApproach);
    }

    private MeasurementOfN2OEmissionPointCategoryAppliedTier getAppliedTier(EtsN2OEmissionPointCategory etsEmissionPointCategory) {
        MeasurementOfN2OEmissionPointCategory n2OEmissionPointCategory =
            determineN2OEmissionPointCategory(etsEmissionPointCategory);

        MeasurementOfN2OMeasuredEmissions n2OMeasuredEmissions = determineN2OMeasuredEmissions(etsEmissionPointCategory);

        AppliedStandard appliedStandard = determineAppliedStandard(etsEmissionPointCategory);

        return MeasurementOfN2OEmissionPointCategoryAppliedTier.builder()
            .emissionPointCategory(n2OEmissionPointCategory)
            .measuredEmissions(n2OMeasuredEmissions)
            .appliedStandard(appliedStandard)
            .build();
    }

    private static AppliedStandard determineAppliedStandard(EtsN2OEmissionPointCategory etsSourceStream) {
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

    private MeasurementOfN2OMeasuredEmissions determineN2OMeasuredEmissions(EtsN2OEmissionPointCategory etsEmissionPointCategory) {
        MeasurementOfN2OMeasuredEmissionsTier n2OMeasuredEmissionsTier =
            StringToEnumConverter.n2OMeasuredEmissionTier(etsEmissionPointCategory.getTierApplied());
        return MeasurementOfN2OMeasuredEmissions.builder()
            .measurementDevicesOrMethods(etsEmissionPointCategory.getMeasurementDevices())
            .samplingFrequency(StringToEnumConverter.measuredSamplingFrequency(etsEmissionPointCategory.getMeasurementFrequency()))
            .tier(n2OMeasuredEmissionsTier)
            .highestRequiredTier(determineHighestRequiredTier(etsEmissionPointCategory, n2OMeasuredEmissionsTier))
            .build();
    }

    private HighestRequiredTier determineHighestRequiredTier(
        EtsN2OEmissionPointCategory etsSourceStream,
        MeasurementOfN2OMeasuredEmissionsTier n2OMeasuredEmissionsTier) {
        if (n2OMeasuredEmissionsTier == MeasurementOfN2OMeasuredEmissionsTier.TIER_3) {
            return null;
        }
        return HighestRequiredTier.builder()
            .isHighestRequiredTier(
                determineIfHighestRequiredTier(
                    n2OMeasuredEmissionsTier,
                    etsSourceStream.getHighestTierAppliedJustification()
                )
            )
            .noHighestRequiredTierJustification(
                determineNoHighestRequiredTierJustification(
                    n2OMeasuredEmissionsTier,
                    etsSourceStream.getHighestTierAppliedJustification()
                )
            )
            .build();
    }

    private static MeasurementOfN2OEmissionPointCategory determineN2OEmissionPointCategory(EtsN2OEmissionPointCategory etsEmissionPointCategory) {
        return MeasurementOfN2OEmissionPointCategory.builder()
            .emissionType(StringToEnumConverter.n2OEmissionType(etsEmissionPointCategory.getN2OEmissionsType()))
            .monitoringApproachType(StringToEnumConverter.n2OMonitoringApproachType(etsEmissionPointCategory.getAppliedApproach()))
            .emissionPoint(etsEmissionPointCategory.getEmissionPoint())
            .categoryType(StringToEnumConverter.categoryType(etsEmissionPointCategory.getEmissionPointCategory()))
            .annualEmittedCO2Tonnes(new BigDecimal(etsEmissionPointCategory.getEstimatedEmission()))
            .emissionSources(etsEmissionPointCategory.getEmissionSources())
            .sourceStreams(etsEmissionPointCategory.getSourceStreams())
            .build();
    }

    private NoHighestRequiredTierJustification determineNoHighestRequiredTierJustification(
        MeasurementOfN2OMeasuredEmissionsTier n2OMeasuredEmissionsTier,
        String highestTierAppliedJustification) {

        if (Objects.isNull(highestTierAppliedJustification) || n2OMeasuredEmissionsTier == MeasurementOfN2OMeasuredEmissionsTier.TIER_3) {
            return null;
        }
        return NoHighestRequiredTierJustification.builder()
            .isTechnicallyInfeasible(Boolean.TRUE)
            .technicalInfeasibilityExplanation(highestTierAppliedJustification)
            .build();
    }

    private Boolean determineIfHighestRequiredTier(
        MeasurementOfN2OMeasuredEmissionsTier n2OMeasuredEmissionsTier,
        String highestTierAppliedJustification) {
        if (Objects.isNull(highestTierAppliedJustification) && n2OMeasuredEmissionsTier != MeasurementOfN2OMeasuredEmissionsTier.TIER_3) {
            return Boolean.TRUE;
        } else if (Objects.nonNull(highestTierAppliedJustification) && n2OMeasuredEmissionsTier != MeasurementOfN2OMeasuredEmissionsTier.TIER_3) {
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }

    private String getN2OEmissionPointCategoryQuery(final List<String> accountIds) {
        final StringBuilder n2OEmissionPointCategoryQueryBuilder = new StringBuilder(QUERY);
        if (!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            n2OEmissionPointCategoryQueryBuilder.append(String.format(" where r.fldEmitterID in (%s)",
                inAccountIdsSql));
        }
        return n2OEmissionPointCategoryQueryBuilder.toString();
    }

    private Map<String, List<EtsN2OEmissionPointCategory>> executeEmissionPointCategoryQuery(
        final String query,
        final List<String> accountIds) {
        final List<EtsN2OEmissionPointCategory> etsEmissionPointCategories =
            migrationJdbcTemplate.query(query,
                new EtsEmissionPointCategoryRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());

        return etsEmissionPointCategories
            .stream()
            .collect(Collectors.groupingBy(EtsN2OEmissionPointCategory::getEtsAccountId));
    }

    private void replaceReferencesWithIds(final Map<String, MeasurementOfN2OMonitoringApproach> sections,
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
        MeasurementOfN2OEmissionPointCategoryAppliedTier tier) {
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
                                                         MeasurementOfN2OEmissionPointCategoryAppliedTier tier) {
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
                                                       MeasurementOfN2OEmissionPointCategoryAppliedTier tier) {
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
                                                      MeasurementOfN2OEmissionPointCategoryAppliedTier tier) {
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
