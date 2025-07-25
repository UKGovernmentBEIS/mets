package uk.gov.pmrv.api.migration.accountidentification;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import java.util.List;

@Log4j2
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
public class MigrationAccountIdentificationService extends MigrationBaseService {
    private final JdbcTemplate migrationJdbcTemplate;
    private final InstallationAccountIdentifierService installationAccountIdentifierService;

    public MigrationAccountIdentificationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
                                                 InstallationAccountIdentifierService installationAccountIdentifierService) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
        this.installationAccountIdentifierService = installationAccountIdentifierService;
    }

    private static final String QUERY_BASE = "select MAX(e.fldEmitterDisplayID) from tblEmitter e";

    @Override
    public List<String> migrate(String ids) {
        Long maxAccountId = migrationJdbcTemplate.queryForObject(QUERY_BASE, Long.class);

        // Update account identifier to max account id
        installationAccountIdentifierService.updateAccountIdentifier(maxAccountId + 500);

        return List.of();
    }

    @Override
    public String getResource() {
        return "account-id";
    }
}
