package uk.gov.pmrv.api.migration.aviationaccount.corsia.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.aviationaccount.common.requests.MigrationInitiateEmpService;

import java.util.List;


@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationInitiateEmpCorsiaService extends MigrationBaseService {

	private final MigrationInitiateEmpService migrationInitiateEmpService;
	
	@Override
    public String getResource() {
        return "initiate-emp-corsia";
    }

    @Override
    public List<String> migrate(String ids) {
    	return migrationInitiateEmpService.migrate(ids, EmissionTradingScheme.CORSIA);
    }
}
