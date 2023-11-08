package uk.gov.pmrv.api.migration.permit.managementprocedures;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.envmanagementsystem.EnvironmentalManagementSystemSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringreporting.MonitoringReportingMigrationService;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProcedures;

import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class ManagementProceduresMigrationService {

    private final MonitoringReportingMigrationService monitoringReportingMigrationService;
    private final DataFlowActivitiesMigrationService dataFlowActivitiesMigrationService;
    private final EnvironmentalManagementSystemSectionMigrationService environmentalManagementSystemSectionMigrationService;
    private final ManagementProceduresDefinitionMigrationService managementProceduresDefinitionMigrationService;

    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {

        initializePermitManagementProcedures(permits);

        monitoringReportingMigrationService.populateSection(accountsToMigratePermit, permits);
        environmentalManagementSystemSectionMigrationService.populateSection(accountsToMigratePermit, permits);
        dataFlowActivitiesMigrationService.populateSection(accountsToMigratePermit, permits);
        managementProceduresDefinitionMigrationService.populateSection(accountsToMigratePermit, permits);
    }

    private void initializePermitManagementProcedures(Map<Long, PermitMigrationContainer> permits) {
        permits.forEach((aLong, permitMigrationContainer) -> {
            ManagementProcedures managementProcedures = ManagementProcedures.builder().build();
            permitMigrationContainer.getPermitContainer().getPermit().setManagementProcedures(managementProcedures);
        });
    }
}
