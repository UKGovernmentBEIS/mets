package uk.gov.pmrv.api.migration.permit.monitoringapproaches.fallback;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationco2.StringToEnumConverter;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class FallbackMonitoringApproachesSourceStreamCategoryMigrationService implements PermitSectionMigrationService<FallbackMonitoringApproach> {

    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY_BASE =
            "with XMLNAMESPACES (\r\n" +
                    "\t'urn:www-toplev-com:officeformsofd' AS fd\r\n" +
                    "), allPermits as (\r\n" +
                    "    select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID,\r\n" +
                    "           FD.fldMajorVersion versionKey\r\n" +
                    "           --, format(P.fldDisplayID, '00') + '|' + format(FD.fldMajorVersion, '0000') + '|' + format(FD.fldMinorVersion, '0000')  versionKey\r\n" +
                    "      from tblForm F\r\n" +
                    "      join tblFormData FD        on FD.fldFormID = F.fldFormID\r\n" +
                    "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n" +
                    "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID\r\n" +
                    "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID\r\n" +
                    "     where P.fldDisplayName = 'Phase 3' and FT.fldName = 'IN_PermitApplication'\r\n" +
                    "), latestVersion as (\r\n" +
                    "    select fldEmitterID, max(versionKey) MaxVersionKey from allPermits group by fldEmitterID\r\n" +
                    "), latestPermit as (\r\n" +
                    "    select p.fldEmitterID, FD.*\r\n" +
                    "    from allPermits p\r\n" +
                    "    join latestVersion v on v.fldEmitterID = p.fldEmitterID and p.versionKey = v.MaxVersionKey and p.fldMinorVersion = 0 \r\n" +
                    "    join tblFormData FD on FD.fldFormDataID = p.fldFormDataID\r\n" +
                    "    join tblEmitter E   on E.fldEmitterID = p.fldEmitterID\r\n" +
                    "    join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and (es.fldDisplayName = 'Live' or e.fldEmitterID in (select fldEmitterID from mig_emitters_explicitly_live))\r\n" +
                    "), calcAppliedTiers as (\r\n" +
                    "    select fldEmitterID,\r\n" +
                    "-- Category\r\n" +
                    "nullif(trim(T.c.query('Calc_tiers_source_stream_refs          ').value('.', 'NVARCHAR(MAX)')), '') Calc_tiers_source_stream_refs,\r\n" +
                    "nullif(trim(T.c.query('Calc_tiers_emission_source_refs      ').value('.', 'NVARCHAR(MAX)')), '') Calc_tiers_emission_source_refs,\r\n" +
                    "nullif(trim(T.c.query('Calc_tiers_estimated_emission          ').value('.', 'NVARCHAR(MAX)')), '') Calc_tiers_estimated_emission,\r\n" +
                    "nullif(trim(T.c.query('Calc_tiers_applied_monitoring_approach ').value('.', 'NVARCHAR(MAX)')), '') Calc_tiers_applied_monitoring_approach,\r\n" +
                    "nullif(trim(T.c.query('Calc_tiers_source_category             ').value('.', 'NVARCHAR(MAX)')), '') Calc_tiers_source_category,\r\n" +
                    "nullif(trim(T.c.query('Calc_tiers_measurement_device_refs     ').value('.', 'NVARCHAR(MAX)')), '') Calc_tiers_measurement_device_refs,\r\n" +
                    "nullif(trim(T.c.query('Calc_tiers_overall_metering_uncertainty').value('.', 'NVARCHAR(MAX)')), '') Calc_tiers_overall_metering_uncertainty\r\n" +
                    " from latestPermit F\r\n" +
                    "cross APPLY fldSubmittedXML.nodes('(fd:formdata/fielddata/Calc_tiers_applied/row)') as T(c)\r\n" +
                    "    where trim(T.c.query('Calc_tiers_applied_monitoring_approach ').value('.', 'NVARCHAR(MAX)')) in ('Fall-back Approach')\r\n" +
                    "    and fldSubmittedXML.value('(fd:formdata/fielddata/Ma_fallback_approach)[1]', 'NVARCHAR(max)') = 'Yes'\r\n" +
                    ")\r\n" +
                    " select fldEmitterID, Calc_tiers_source_stream_refs, Calc_tiers_emission_source_refs, Calc_tiers_estimated_emission,\r\n" +
                    "       Calc_tiers_source_category, Calc_tiers_measurement_device_refs, Calc_tiers_overall_metering_uncertainty\r\n" +
                    "  from calcAppliedTiers \r\n"
            ;

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {

        List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        Map<String, FallbackMonitoringApproach> sections = queryEtsSection(accountIds);

        replaceReferencesWithIds(sections, permits, accountsToMigratePermit);

        sections.forEach((etsAccId, section) -> {
            final PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());

            final FallbackMonitoringApproach fallbackMonitoringApproach =
                    (FallbackMonitoringApproach)permitMigrationContainer
                            .getPermitContainer()
                            .getPermit()
                            .getMonitoringApproaches()
                            .getMonitoringApproaches()
                            .get(MonitoringApproachType.FALLBACK);
            fallbackMonitoringApproach.setSourceStreamCategoryAppliedTiers(
                    sections.get(etsAccId).getSourceStreamCategoryAppliedTiers()
            );
        });
    }

    @Override
    public Map<String, FallbackMonitoringApproach> queryEtsSection(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("where calcAppliedTiers.fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();

        Map<String, List<EtsFallbackSourceStreamCategory>> allFallbackSourceStreamCategories = executeQuery(query, accountIds);

        Map<String, FallbackMonitoringApproach> fallbackMonitoringApproaches = new HashMap<>();
        allFallbackSourceStreamCategories.forEach((accountId, sourceStreams) -> {
            List<FallbackSourceStreamCategoryAppliedTier> appliedTiers = sourceStreams.stream().map(sourceStream -> FallbackSourceStreamCategoryAppliedTier.builder()
                   .sourceStreamCategory(FallbackSourceStreamCategory.builder()
                           .sourceStream(sourceStream.getSourceStream())
                           .emissionSources(sourceStream.getEmissionSources())
                           .annualEmittedCO2Tonnes(new BigDecimal(sourceStream.getEstimatedEmission()))
                           .categoryType(StringToEnumConverter.sourceStreamCategoryType(sourceStream.getSourceStreamCategory()))
                           .measurementDevicesOrMethods(sourceStream.getMeasurementDevices())
                           .uncertainty(StringToEnumConverter.meteringUncertainty(sourceStream.getMeteringUncertainty()))
                           .build())
                   .build()).collect(Collectors.toList());
            final FallbackMonitoringApproach fallbackMonitoringApproach = FallbackMonitoringApproach.builder()
                    .sourceStreamCategoryAppliedTiers(appliedTiers)
                    .build();
            fallbackMonitoringApproaches.put(accountId, fallbackMonitoringApproach);
        });
        return fallbackMonitoringApproaches;
    }

    private Map<String, List<EtsFallbackSourceStreamCategory>> executeQuery(String query, List<String> accountIds) {
        final List<EtsFallbackSourceStreamCategory> etsFallbackSourceStreamCategories =
                migrationJdbcTemplate.query(query,
                        new EtsFallbackSourceStreamCategoryRowMapper(),
                        accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());

        return etsFallbackSourceStreamCategories
                .stream()
                .collect(Collectors.groupingBy(EtsFallbackSourceStreamCategory::getEtsAccountId));
    }

    private void replaceReferencesWithIds(final Map<String, FallbackMonitoringApproach> sections,
                                          final Map<Long, PermitMigrationContainer> permits,
                                          final Map<String, Account> accountsToMigratePermit) {

        sections.forEach((id, approach) -> {

                    final Long accountId = accountsToMigratePermit.get(id).getId();
                    final Permit permit = permits.get(accountId).getPermitContainer().getPermit();
                    final List<SourceStream> sourceStreams = permit.getSourceStreams().getSourceStreams();
                    final List<EmissionSource> emissionSources = permit.getEmissionSources().getEmissionSources();
                    final List<MeasurementDeviceOrMethod> measurementDevicesOrMethods = permit.getMeasurementDevicesOrMethods().getMeasurementDevicesOrMethods();

                    approach.getSourceStreamCategoryAppliedTiers().forEach(tier -> {

                                // source stream
                                final String sourceStreamRef = tier.getSourceStreamCategory().getSourceStream();
                                final String sourceStreamId = sourceStreams.stream()
                                        .filter(ss -> ss.getReference().trim().equalsIgnoreCase(sourceStreamRef.trim()))
                                        .findAny()
                                        .map(SourceStream::getId)
                                        .orElseGet(() -> {
                                            log.error(String.format("cannot find source stream with reference %s for emitter %s",
                                                    sourceStreamRef,
                                                    id));
                                            return sourceStreamRef;
                                        });
                                tier.getSourceStreamCategory().setSourceStream(sourceStreamId);

                                // emission sources
                                final Set<String> emissionSourcesIds =
                                        tier.getSourceStreamCategory().getEmissionSources().stream().map(
                                                emissionSourceRef -> emissionSources.stream()
                                                        .filter(es -> es.getReference().trim().equalsIgnoreCase(emissionSourceRef.trim()))
                                                        .findAny()
                                                        .map(EmissionSource::getId)
                                                        .orElseGet(() -> {
                                                            log.error(String.format("cannot find emission source with reference %s for emitter %s",
                                                                    emissionSourceRef,
                                                                    id));
                                                            return emissionSourceRef;
                                                        })
                                        ).collect(Collectors.toSet());
                                tier.getSourceStreamCategory().setEmissionSources(emissionSourcesIds);

                                // measurement devices
                                final Set<String> deviceIds = tier.getSourceStreamCategory().getMeasurementDevicesOrMethods().stream().map(
                                        deviceRef -> measurementDevicesOrMethods.stream()
                                                .filter(dev -> dev.getReference().trim().equalsIgnoreCase(deviceRef.trim()))
                                                .findAny()
                                                .map(MeasurementDeviceOrMethod::getId)
                                                .orElseGet(() -> {
                                                    log.error(String.format("cannot find measurement device with reference %s for emitter %s",
                                                            deviceRef,
                                                            id));
                                                    return deviceRef;
                                                })
                                ).collect(Collectors.toSet());
                                tier.getSourceStreamCategory().setMeasurementDevicesOrMethods(deviceIds);
                            }
                    );
                }
        );
    }
}
