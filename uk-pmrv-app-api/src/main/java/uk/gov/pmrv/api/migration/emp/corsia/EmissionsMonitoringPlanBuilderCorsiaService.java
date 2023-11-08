package uk.gov.pmrv.api.migration.emp.corsia;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.corsia.monitoringapproach.EmpMonitoringApproachCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.operatordetails.EmpOperatorDetailsCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.abbreviations.EmpAbbreviationsCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.additionaldocuments.EmpAdditionalDocumentsCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.emissionsources.EmpEmissionSourcesCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.fummethods.EmpFuelUseMonitoringMethodsCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.datagaps.EmpDataGapsCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.managementprocedures.EmpManagementProceduresCorsiaSectionMigrationService;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmissionsMonitoringPlanBuilderCorsiaService {

	private final AviationAccountRepository aviationAccountRepository;
	
	private final EmpOperatorDetailsCorsiaSectionMigrationService operatorDetailsCorsiaSectionMigrationService;
	private final EmpMonitoringApproachCorsiaSectionMigrationService monitoringApproachSectionMigrationService;
	private final EmpEmissionSourcesCorsiaSectionMigrationService emissionSourcesSectionMigrationService;
	private final EmpFuelUseMonitoringMethodsCorsiaSectionMigrationService fuelUseMonitoringMethodsSectionMigrationService;
	private final EmpDataGapsCorsiaSectionMigrationService dataGapsSectionMigrationService;
	private final EmpManagementProceduresCorsiaSectionMigrationService managementProceduresSectionMigrationService;
	private final EmpAdditionalDocumentsCorsiaSectionMigrationService additionalDocumentsSectionMigrationService;
	private final EmpAbbreviationsCorsiaSectionMigrationService abbreviationsSectionMigrationService;

    public Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> buildEmps(List<String> accountIds, List<String> migrationResults) {

        Map<String, Account> migratedAccounts = findMigratedAccountsToMigrateEmp(accountIds, migrationResults);

        if (migratedAccounts.isEmpty()) {
            migrationResults.add("No migrated accounts found!");
            return Map.of();
        }

        // initialize emps
        Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emissionMonitoringPlans = new HashMap<>();
        initializeEmps(migratedAccounts.values(), emissionMonitoringPlans);
        
        operatorDetailsCorsiaSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        monitoringApproachSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        emissionSourcesSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        fuelUseMonitoringMethodsSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        dataGapsSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        managementProceduresSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        abbreviationsSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);
        additionalDocumentsSectionMigrationService.populateSection(migratedAccounts, emissionMonitoringPlans);

        
        final EmissionsMonitoringPlanCorsia emptyEmp = EmissionsMonitoringPlanCorsia.builder().build();
        return emissionMonitoringPlans.entrySet().stream()
                .filter(entry -> !entry.getValue().getEmpContainer().getEmissionsMonitoringPlan().equals(emptyEmp))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Account> findMigratedAccountsToMigrateEmp(List<String> accountIds, List<String> migrationResults) {

    	Map<String, Account> allMigratedAccounts = aviationAccountRepository.findAllByEmissionTradingSchemeAndStatusInAndMigratedAccountIdNotNull(
        		EmissionTradingScheme.CORSIA, List.of(AviationAccountStatus.LIVE)).stream()
                .collect(Collectors.toMap(Account::getMigratedAccountId, Function.identity()));

        if (accountIds.isEmpty()) {
            return new HashMap<>(allMigratedAccounts);
        }

        Map<String, Account> accountsToMigrateEmp = allMigratedAccounts.entrySet()
                .stream()
                .filter(entry -> accountIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        accountIds.removeAll(accountsToMigrateEmp.keySet());
        accountIds.forEach(notFoundAccountId ->
                migrationResults.add("account id: " + notFoundAccountId + " not found"));
        return accountsToMigrateEmp;

    }

    private void initializeEmps(Collection<Account> migratedAccounts, Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emissionMonitoringPlans) {

        migratedAccounts
                .forEach(account ->
                        emissionMonitoringPlans.put(account.getId(),
                                EmissionsMonitoringPlanMigrationCorsiaContainer.builder()
                                        .empContainer(EmissionsMonitoringPlanCorsiaContainer.builder()
                                                .scheme(EmissionTradingScheme.CORSIA)
                                                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                                                		.abbreviations(EmpAbbreviations.builder().exist(false).build())
                                                		.build())
                                                .build())
                                        .build()));
    }
}
