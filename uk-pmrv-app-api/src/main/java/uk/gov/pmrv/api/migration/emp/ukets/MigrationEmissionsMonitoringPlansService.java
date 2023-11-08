package uk.gov.pmrv.api.migration.emp.ukets;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationEmissionsMonitoringPlansService extends MigrationBaseService {

    private final MigrationEmissionsMonitoringPlanService migrationEmpService;

    private final EmissionsMonitoringPlanBuilderService empBuilderService;


    @Override
    public List<String> migrate(String etsAccountIdsString) {
        List<String> migrationResults = new ArrayList<>();

        List<String> etsAccountIds = !StringUtils.isBlank(etsAccountIdsString)
                ? new ArrayList<>(Arrays.asList(etsAccountIdsString.split("\\s*,\\s*")))
                : new ArrayList<>();

        Map<Long, EmissionsMonitoringPlanMigrationContainer> emissionMonitoringPlans =
                empBuilderService.buildEmps(etsAccountIds, migrationResults);
        int failedCounter = 0;
        for (Map.Entry<Long, EmissionsMonitoringPlanMigrationContainer> entry : emissionMonitoringPlans.entrySet()) {
            try {
                migrationEmpService.migrateEmp(entry.getKey(), entry.getValue(), migrationResults);
            } catch (Exception e) {
                log.error(e.getMessage());
                failedCounter++;
            }
        }
        migrationResults.add("Migration of emissions monitoring plans results: " + failedCounter + "/" + emissionMonitoringPlans.size() + " failed");
        return migrationResults;
    }

    @Override
    public String getResource() {
        return "emps";
    }
}
