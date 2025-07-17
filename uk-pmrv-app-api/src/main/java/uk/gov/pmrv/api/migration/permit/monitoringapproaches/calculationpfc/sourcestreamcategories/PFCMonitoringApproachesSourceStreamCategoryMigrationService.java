package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationpfc.sourcestreamcategories;

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
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class PFCMonitoringApproachesSourceStreamCategoryMigrationService implements PermitSectionMigrationService<CalculationOfPFCMonitoringApproach> {

    private final JdbcTemplate migrationJdbcTemplate;

    public PFCMonitoringApproachesSourceStreamCategoryMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
    }

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
                    "   select p.fldEmitterID, FD.*\r\n" +
                    "  from allPermits p\r\n" +
                    "  join latestVersion v on v.fldEmitterID = p.fldEmitterID and p.versionKey = v.MaxVersionKey and p.fldMinorVersion = 0 \r\n" +
                    "  join tblFormData FD on FD.fldFormDataID = p.fldFormDataID\r\n" +
                    "  join tblEmitter E   on E.fldEmitterID = p.fldEmitterID\r\n" +
                    "  join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and (es.fldDisplayName = 'Live' or e.fldEmitterID in (select fldEmitterID from mig_emitters_explicitly_live))\r\n" +
                    "), pfcAppliedTiers as (\r\n" +
                    "select fldEmitterID,\r\n" +
                    "-- Category\r\n" +
                    "nullif(trim(T.c.query('Mpfc_source_stream_refs          ').value('.', 'NVARCHAR(MAX)')), '') Mpfc_source_stream_refs          ,\r\n" +
                    "nullif(trim(T.c.query('Mpfc_emission_point_refs         ').value('.', 'NVARCHAR(MAX)')), '') Mpfc_emission_point_refs         ,\r\n" +
                    "nullif(trim(T.c.query('Mpfc_emission_source_refs        ').value('.', 'NVARCHAR(MAX)')), '') Mpfc_emission_source_refs        ,\r\n" +
                    "nullif(trim(T.c.query('Mpfc_estimated_annual_emissions  ').value('.', 'NVARCHAR(MAX)')), '') Mpfc_estimated_annual_emissions  ,\r\n" +
                    "nullif(trim(T.c.query('Mpfc_calculation_method_applied  ').value('.', 'NVARCHAR(MAX)')), '') Mpfc_calculation_method_applied  ,\r\n" +
                    "nullif(trim(T.c.query('Mpfc_source_stream_category      ').value('.', 'NVARCHAR(MAX)')), '') Mpfc_source_stream_category      ,\r\n" +
                    "nullif(trim(T.c.query('Mpfc_highest_tiers_justification ').value('.', 'NVARCHAR(MAX)')), '') Mpfc_highest_tiers_justification ,\r\n" +
                    "-- Activity data                                                                                                  ,\r\n" +
                    "nullif(trim(T.c.query('Mpfc_is_mass_balance_approach    ').value('.', 'NVARCHAR(MAX)')), '') Mpfc_is_mass_balance_approach    ,\r\n" +
                    "nullif(trim(T.c.query('Mpfc_activity_data_tier_applied  ').value('.', 'NVARCHAR(MAX)')), '') Mpfc_activity_data_tier_applied  ,\r\n" +
                    "-- Emission factor                                                                                                ,\r\n" +
                    "nullif(trim(T.c.query('Mpfc_emission_factor_tier_applied').value('.', 'NVARCHAR(MAX)')), '') Mpfc_emission_factor_tier_applied \r\n" +
                    " from latestPermit F\r\n" +
                    "cross APPLY fldSubmittedXML.nodes('(fd:formdata/fielddata/Mpfc_emissions/row)') as T(c)\r\n" +
                    "), mig_pfc_categories as (\r\n" +
                    "select *,\r\n" +
                    "            case when Mpfc_is_mass_balance_approach = 'Yes' and Mpfc_activity_data_tier_applied in ('Tier 1', 'Tier 2', 'Tier 3') or\r\n" +
                    "                      Mpfc_is_mass_balance_approach = 'No'  and Mpfc_activity_data_tier_applied in ('Tier 1') then 1 else 0 end ad_is_middle_tier,\r\n" +
                    "            case when Mpfc_emission_factor_tier_applied in ('Tier 1') then 1 else 0 end ef_is_middle_tier\r\n" +
                    "  from pfcAppliedTiers c\r\n" +
                    ")\r\n" +
                    "select fldEmitterID, Mpfc_source_stream_refs, Mpfc_emission_point_refs, Mpfc_emission_source_refs, Mpfc_estimated_annual_emissions,\r\n" +
                    "       Mpfc_calculation_method_applied, Mpfc_source_stream_category, Mpfc_highest_tiers_justification,\r\n" +
                    "       Mpfc_is_mass_balance_approach, Mpfc_activity_data_tier_applied, ad_is_middle_tier,\r\n" +
                    "       Mpfc_emission_factor_tier_applied, ef_is_middle_tier\r\n" +
                    "  from mig_pfc_categories \r\n";

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {

        final List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        Map<String, CalculationOfPFCMonitoringApproach> sections = queryEtsSection(accountIds);

        replaceReferencesWithIds(sections, permits, accountsToMigratePermit);

        sections.forEach((etsAccId, section) -> {
            final PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());

            final CalculationOfPFCMonitoringApproach pfcMonitoringApproach =
                    (CalculationOfPFCMonitoringApproach)permitMigrationContainer
                            .getPermitContainer()
                            .getPermit()
                            .getMonitoringApproaches()
                            .getMonitoringApproaches()
                            .get(MonitoringApproachType.CALCULATION_PFC);
            pfcMonitoringApproach.setSourceStreamCategoryAppliedTiers(
                    sections.get(etsAccId).getSourceStreamCategoryAppliedTiers()
            );
        });
    }

    @Override
    public Map<String, CalculationOfPFCMonitoringApproach> queryEtsSection(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if (!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("where mig_pfc_categories.fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();

        Map<String, List<EtsPFCSourceStreamCategory>> allPFCSourceStreamCategories = executeQuery(query, accountIds);
        Map<String, CalculationOfPFCMonitoringApproach> pfcMonitoringApproaches = new HashMap<>();
        allPFCSourceStreamCategories.forEach((etsAccountId, sourceStreams) -> {
            final List<PFCSourceStreamCategoryAppliedTier> appliedTiers = sourceStreams.stream().map(
                            sourceStream -> PFCSourceStreamCategoryAppliedTier.builder()
                                    .sourceStreamCategory(PFCApproachSourceStreamMapper.constructPFCSourceStreamCategory(sourceStream))
                                    .activityData(PFCApproachSourceStreamMapper.constructPFCActivityData(sourceStream))
                                    .emissionFactor(PFCApproachSourceStreamMapper.constructPFCEmissionFactor(sourceStream))
                                    .build())
                    .collect(Collectors.toList());
            CalculationOfPFCMonitoringApproach pfcMonitoringApproach = CalculationOfPFCMonitoringApproach.builder()
                    .sourceStreamCategoryAppliedTiers(appliedTiers)
                    .build();
            pfcMonitoringApproaches.put(etsAccountId, pfcMonitoringApproach);
        });
        return pfcMonitoringApproaches;
    }

    private Map<String, List<EtsPFCSourceStreamCategory>> executeQuery(String query, List<String> accountIds) {
        List<EtsPFCSourceStreamCategory> etsPFCSourceStreamCategories = migrationJdbcTemplate.query(query,
                new EtsPFCSourceStreamCategoryRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());

        return etsPFCSourceStreamCategories
                .stream()
                .collect(Collectors.groupingBy(EtsPFCSourceStreamCategory::getEtsAccountId));
    }

    private void replaceReferencesWithIds(Map<String, CalculationOfPFCMonitoringApproach> sections, Map<Long, PermitMigrationContainer> permits, Map<String, Account> accountsToMigratePermit) {

        sections.forEach((id, approach) -> {
            final Long accountId = accountsToMigratePermit.get(id).getId();
            final Permit permit = permits.get(accountId).getPermitContainer().getPermit();
            final List<SourceStream> sourceStreams = permit.getSourceStreams().getSourceStreams();
            final List<EmissionSource> emissionSources = permit.getEmissionSources().getEmissionSources();
            final List<EmissionPoint> emissionPoints = permit.getEmissionPoints().getEmissionPoints();

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
                final Set<String> emissionPointIds = tier.getSourceStreamCategory().getEmissionPoints().stream().map(
                        emissionPoint -> emissionPoints.stream()
                                .filter(emp -> emp.getReference().trim().equalsIgnoreCase(emissionPoint.trim()))
                                .findAny()
                                .map(EmissionPoint::getId)
                                .orElseGet(() -> {
                                    log.error(String.format("cannot find emission point with reference %s for emitter %s",
                                            emissionPoint,
                                            id));
                                    return emissionPoint;
                                })
                ).collect(Collectors.toSet());
                tier.getSourceStreamCategory().setEmissionPoints(emissionPointIds);
            });
        });
    }
}
