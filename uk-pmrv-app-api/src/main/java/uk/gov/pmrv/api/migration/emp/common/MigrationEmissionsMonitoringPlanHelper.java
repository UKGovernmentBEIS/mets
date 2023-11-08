package uk.gov.pmrv.api.migration.emp.common;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;

@UtilityClass
public class MigrationEmissionsMonitoringPlanHelper {

    public static String constructSectionQuery(String query, List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(query);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("where e.fldEmitterID in (%s)", inAccountIdsSql));
        }
        return queryBuilder.toString();
    }
}
