package uk.gov.pmrv.api.migration.emp.ukets.emissionsources;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanMigrationContainer;
import uk.gov.pmrv.api.migration.emp.ukets.emissionsources.aircrafttypedetails.EmpAircraftTypeDetailsMigrationService;
import uk.gov.pmrv.api.migration.emp.ukets.emissionsources.emissionsourcesdetails.EmpEmissionSourcesDetailsMigrationService;

import java.util.Map;


@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpEmissionSourcesSectionMigrationService {

	private final EmpEmissionSourcesDetailsMigrationService emissionSourcesDetailsMigrationService;
    private final EmpAircraftTypeDetailsMigrationService aircraftTypeDetailsMigrationService;
    
    public void populateSection(Map<String, Account> accountsToMigratePermit,
            Map<Long, EmissionsMonitoringPlanMigrationContainer> emps) {

    	emissionSourcesDetailsMigrationService.populateSection(accountsToMigratePermit, emps);
    	aircraftTypeDetailsMigrationService.populateSection(accountsToMigratePermit, emps);
    }
}
