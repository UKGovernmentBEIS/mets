package uk.gov.pmrv.api.migration.emp.ukets;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

import java.util.List;
import java.util.Map;

public interface EmissionsMonitoringPlanSectionMigrationService<T extends EmpUkEtsSection> {

    void populateSection(Map<String, Account> accountsToMigrate,
                         Map<Long, EmissionsMonitoringPlanMigrationContainer> emps);

    Map<String, T> queryEtsSection(List<String> accountIds);
}
