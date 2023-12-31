package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurementco2;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;

import java.util.Map;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MeasMonitoringApproachesMigrationService {

    private final MeasMonitoringApproachesDetailsMigrationService measMonitoringApproachesDetailsMigrationService;
    private final MeasMonitoringApproachesEmissionPointCategoryMigrationService measMonitoringApproachesEmissionPointCategoryMigrationService;


    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        measMonitoringApproachesDetailsMigrationService.populateSection(accountsToMigratePermit, permits);
        measMonitoringApproachesEmissionPointCategoryMigrationService.populateSection(accountsToMigratePermit, permits);
    }
}
