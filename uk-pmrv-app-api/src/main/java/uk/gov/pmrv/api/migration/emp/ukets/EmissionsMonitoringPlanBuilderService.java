package uk.gov.pmrv.api.migration.emp.ukets;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.ukets.abbreviations.EmpAbbreviationsSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.additionaldocuments.EmpAdditionalDocumentsSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.datagaps.EmpDataGapsSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.emissionsources.EmpEmissionSourcesSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.emissionsreductionclaim.EmpEmissionsReductionClaimSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.fummethods.EmpFuelUseMonitoringMethodsSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.latesubmission.EmpApplicationTimeframeInfoSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.managementprocedures.EmpManagementProceduresSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.monitoringapproach.EmpMonitoringApproachSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.operatordetails.EmpOperatorDetailsSectionMigrationService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmissionsMonitoringPlanBuilderService {

    private final AviationAccountRepository aviationAccountRepository;

    private final EmpOperatorDetailsSectionMigrationService operatorDetailsSectionMigrationService;
    private final EmpEmissionsReductionClaimSectionMigrationService emissionsReductionClaimSectionMigrationService;
    private final EmpAbbreviationsSectionMigrationService abbreviationsSectionMigrationService;
    private final EmpAdditionalDocumentsSectionMigrationService additionalDocumentsSectionMigrationService;
    private final EmpMonitoringApproachSectionMigrationService monitoringApproachSectionMigrationService;
    private final EmpApplicationTimeframeInfoSectionMigrationService applicationTimeframeInfoSectionMigrationService;
    private final EmpDataGapsSectionMigrationService dataGapsSectionMigrationService;
    private final EmpEmissionSourcesSectionMigrationService emissionSourcesSectionMigrationService;
    private final EmpFuelUseMonitoringMethodsSectionMigrationService fuelUseMonitoringMethodsSectionMigrationService;
    private final EmpManagementProceduresSectionMigrationService managementProceduresSectionMigrationService;

    public Map<Long, EmissionsMonitoringPlanMigrationContainer> buildEmps(List<String> etsAccountIds, List<String> migrationResults) {

        Map<String, Account> migratedAccounts = findMigratedAccountsToMigrateEmp(etsAccountIds, migrationResults);

        if (migratedAccounts.isEmpty()) {
            migrationResults.add("No migrated accounts found!");
            return Map.of();
        }

        // initialize emps
        Map<Long, EmissionsMonitoringPlanMigrationContainer> emissionMonitoringPlans = new HashMap<>();
        initializeEmps(migratedAccounts.values(), emissionMonitoringPlans);

        operatorDetailsSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        monitoringApproachSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        emissionsReductionClaimSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        applicationTimeframeInfoSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        dataGapsSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        abbreviationsSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        additionalDocumentsSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        emissionSourcesSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        fuelUseMonitoringMethodsSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        managementProceduresSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);

        final EmissionsMonitoringPlanUkEts emptyEmp = EmissionsMonitoringPlanUkEts.builder().build();
        return emissionMonitoringPlans.entrySet().stream()
                .filter(entry -> !entry.getValue().getEmpContainer().getEmissionsMonitoringPlan().equals(emptyEmp))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Account> findMigratedAccountsToMigrateEmp(List<String> etsAccountIds, List<String> migrationResults) {

        Map<String, Account> allMigratedAccounts = aviationAccountRepository.findAllByEmissionTradingSchemeAndStatusInAndMigratedAccountIdNotNull(
        		EmissionTradingScheme.UK_ETS_AVIATION, List.of(AviationAccountStatus.LIVE)).stream()
                .collect(Collectors.toMap(Account::getMigratedAccountId, Function.identity()));

        if (etsAccountIds.isEmpty()) {
            return new HashMap<>(allMigratedAccounts);
        }

        Map<String, Account> accountsToMigrateEmp = allMigratedAccounts.entrySet()
                .stream()
                .filter(entry -> etsAccountIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        etsAccountIds.removeAll(accountsToMigrateEmp.keySet());
        etsAccountIds.forEach(notFoundEtsAccountId ->
                migrationResults.add("Ets account id: " + notFoundEtsAccountId + " not found"));
        return accountsToMigrateEmp;

    }

    private void initializeEmps(Collection<Account> migratedAccounts, Map<Long, EmissionsMonitoringPlanMigrationContainer> emissionMonitoringPlans) {

        migratedAccounts
                .forEach(account ->
                        emissionMonitoringPlans.put(account.getId(),
                                EmissionsMonitoringPlanMigrationContainer.builder()
                                        .empContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
                                                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                                                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                                                        .abbreviations(EmpAbbreviations.builder().exist(false).build()).build())
                                                .build())
                                        .build()));
    }
}
