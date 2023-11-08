package uk.gov.pmrv.api.migration.emp.corsia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationEmissionsMonitoringPlansCorsiaService extends MigrationBaseService {

	private final MigrationEmissionsMonitoringPlanCorsiaService migrationEmpService;

    private final EmissionsMonitoringPlanBuilderCorsiaService empBuilderService;


    @Override
    public List<String> migrate(String accountIdsString) {
        List<String> migrationResults = new ArrayList<>();

        List<String> accountIds = !StringUtils.isBlank(accountIdsString)
                ? new ArrayList<>(Arrays.asList(accountIdsString.split("\\s*,\\s*")))
                : new ArrayList<>();

        Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emissionMonitoringPlans =
                empBuilderService.buildEmps(accountIds, migrationResults);
        int failedCounter = 0;
        for (Map.Entry<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> entry : emissionMonitoringPlans.entrySet()) {
            try {
                migrationEmpService.migrateEmp(entry.getKey(), entry.getValue(), migrationResults);
            } catch (Exception e) {
                log.error(e.getMessage());
                failedCounter++;
            }
        }
        migrationResults.add("Migration of CORSIA emissions monitoring plans results: " + failedCounter + "/" + emissionMonitoringPlans.size() + " failed");
        return migrationResults;
    }

    @Override
    public String getResource() {
        return "emps-corsia";
    }
}
