package uk.gov.pmrv.api.migration.permit;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.permit.domain.PermitSection;

import java.util.List;
import java.util.Map;

public interface PermitSectionMigrationService<T extends PermitSection> {
    
    void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits);

    Map<String, T> queryEtsSection(List<String> accountIds);
}
