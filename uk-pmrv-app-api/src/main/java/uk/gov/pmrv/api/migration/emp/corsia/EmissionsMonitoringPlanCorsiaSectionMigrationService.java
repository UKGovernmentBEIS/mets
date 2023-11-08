package uk.gov.pmrv.api.migration.emp.corsia;

import java.util.List;
import java.util.Map;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;

public interface EmissionsMonitoringPlanCorsiaSectionMigrationService <T extends EmpCorsiaSection> {

	void populateSection(Map<String, Account> accountsToMigrate,
            Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emps);

	Map<String, T> querySection(List<String> accountIds);
}
