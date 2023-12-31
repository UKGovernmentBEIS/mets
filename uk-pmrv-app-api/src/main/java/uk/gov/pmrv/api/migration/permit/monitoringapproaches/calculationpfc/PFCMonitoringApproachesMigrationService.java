package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationpfc;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationpfc.cellanodetypes.PFCMonitoringApproachesCellAndAnodeTypesMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationpfc.details.PFCMonitoringApproachesDetailsMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationpfc.sourcestreamcategories.PFCMonitoringApproachesSourceStreamCategoryMigrationService;

import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class PFCMonitoringApproachesMigrationService {

    private final PFCMonitoringApproachesDetailsMigrationService pfcMonitoringApproachesDetailsMigrationService;
    private final PFCMonitoringApproachesCellAndAnodeTypesMigrationService pfcMonitoringApproachesCellAndAnodeTypesMigrationService;

    private final PFCMonitoringApproachesSourceStreamCategoryMigrationService pfcMonitoringApproachesSourceStreamCategoryMigrationService;

    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        pfcMonitoringApproachesDetailsMigrationService.populateSection(accountsToMigratePermit, permits);
        pfcMonitoringApproachesCellAndAnodeTypesMigrationService.populateSection(accountsToMigratePermit, permits);
        pfcMonitoringApproachesSourceStreamCategoryMigrationService.populateSection(accountsToMigratePermit, permits);
    }
}
