package uk.gov.pmrv.api.migration.emp.corsia.operatordetails;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanMigrationCorsiaContainer;
import uk.gov.pmrv.api.migration.emp.corsia.operatordetails.subsidiarycompanies.EmpSubsidiaryCompaniesCorsiaMigrationService;

import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpOperatorDetailsCorsiaSectionMigrationService {

	private final EmpOperatorDetailsCorsiaMigrationService operatorDetailsCorsiaMigrationService;
    private final EmpSubsidiaryCompaniesCorsiaMigrationService subsidiaryCompaniesCorsiaMigrationService;
    
    public void populateSection(Map<String, Account> accountsToMigratePermit,
            Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emps) {

    	operatorDetailsCorsiaMigrationService.populateSection(accountsToMigratePermit, emps);
    	subsidiaryCompaniesCorsiaMigrationService.populateSection(accountsToMigratePermit, emps);
    }
}
