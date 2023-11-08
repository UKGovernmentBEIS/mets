package uk.gov.pmrv.api.migration.aviationaccount.common.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.aviationaccount.common.AviationAccountHelper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.service.EmissionMonitoringPlanCreationService;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationInitiateEmpService {

	private final AviationAccountRepository aviationAccountRepository;
    private final EmissionMonitoringPlanCreationService emissionMonitoringPlanCreationService;
    
	public List<String> migrate(String ids, EmissionTradingScheme scheme) {
        List<String> results = new ArrayList<>();
        List<AviationAccount> accounts;
        
        if (StringUtils.isBlank(ids)) {
        	accounts = aviationAccountRepository.findAllByEmissionTradingSchemeAndStatusInAndMigratedAccountIdNotNull(
        			scheme, List.of(AviationAccountStatus.NEW));
        } else {
        	accounts = aviationAccountRepository.findByEmissionTradingSchemeAndStatusInAndMigratedAccountIdIn(
        			scheme, List.of(AviationAccountStatus.NEW), new ArrayList<>(Arrays.asList(ids.split(","))));
        }        

        AtomicInteger failedCounter = new AtomicInteger(0);
        for (AviationAccount account: accounts) {
            List<String> migrationResults = doMigrate(account, scheme, failedCounter);
            results.addAll(migrationResults);
        }

        results.add("migration of aviation account requests results: " + failedCounter + "/" + accounts.size() + " failed");
        return results;
    }

    private List<String> doMigrate(AviationAccount account, EmissionTradingScheme scheme, AtomicInteger failedCounter) {
    	List<String> results = new ArrayList<>();
        
        try {
        	emissionMonitoringPlanCreationService.createRequestEmissionMonitoringPlan(
        			account.getId(), scheme);

            results.add(AviationAccountHelper.constructSuccessMessage(account.getMigratedAccountId(), 
            		account.getName(), String.valueOf(account.getId()), account.getCompetentAuthority().toString()));
        } catch (Exception ex) {
            failedCounter.incrementAndGet();
            log.error("migration of aviation account request: {} failed with {}",
            		account.getMigratedAccountId(), ExceptionUtils.getRootCause(ex).getMessage());
            results.add(AviationAccountHelper.constructErrorMessageWithCA(account.getMigratedAccountId(), 
            		account.getName(), String.valueOf(account.getId()), account.getCompetentAuthority().toString(),
                    ExceptionUtils.getRootCause(ex).getMessage(), null));
        }
        return results;
    }
}
