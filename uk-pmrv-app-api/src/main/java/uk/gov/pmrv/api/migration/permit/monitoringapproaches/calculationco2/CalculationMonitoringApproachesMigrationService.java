package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationco2;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;

import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class CalculationMonitoringApproachesMigrationService {

    private final CalculationMonitoringApproachesDetailsMigrationService calculationMonitoringApproachesDetailsMigrationService;
    private final CalculationMonitoringApproachesSourceStreamCategoryMigrationService calculationMonitoringApproachesSourceStreamCategoryMigrationService;


    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        
        calculationMonitoringApproachesDetailsMigrationService.populateSection(accountsToMigratePermit, permits);
        calculationMonitoringApproachesSourceStreamCategoryMigrationService.populateSection(accountsToMigratePermit, permits);
    }
}
