package uk.gov.pmrv.api.migration.aviationaccount.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountCreationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MigrationAviationAccountService {

	private final Validator validator;
    private final AviationAccountDTOBuilder aviationAccountDTOBuilder;
    private final MigrationAviationAccountCreationService migrationAviationAccountCreationService;
    
	public List<String> doMigrate(AviationEmitter emitter, AtomicInteger failedCounter, EmissionTradingScheme scheme) {
    	List<String> results = new ArrayList<>();
    	LocationOnShoreStateDTO locationDTO = new LocationOnShoreStateDTO();
    	Set<ConstraintViolation<LocationOnShoreStateDTO>> locationViolations = new HashSet<>();
        String accountId = emitter.getFldEmitterId();

        AviationAccountCreationDTO accountDTO = aviationAccountDTOBuilder.constructAviationAccountDTO(emitter, scheme);        
        ReportingStatusReason reason = aviationAccountDTOBuilder.constructReportingStatusReason(emitter);

        // validate aviation account dto and reporting status reason
        Set<ConstraintViolation<AviationAccountCreationDTO>> accountViolations = validator.validate(accountDTO);
        Set<ConstraintViolation<ReportingStatusReason>> reportingStatusReasonViolations = validator.validate(reason);
        
        // create and validate location dto if account has contact address
        if (emitter.getAddressLine1() != null || "LIVE".equalsIgnoreCase(emitter.getEmitterStatus())) {
        	locationDTO = aviationAccountDTOBuilder.constructLocationDTO(emitter);
        	locationViolations = validator.validate(locationDTO);
        }
        
        if (!accountViolations.isEmpty() || !reportingStatusReasonViolations.isEmpty() || !locationViolations.isEmpty()) {
        	accountViolations.forEach(v ->
                    results.add(AviationAccountHelper.constructErrorMessageWithCA(accountId, accountDTO.getName(), emitter.getEmitterDisplayId(), emitter.getCa(),
                            v.getMessage(), v.getPropertyPath().iterator().next().getName() + ":" + v.getInvalidValue())));
        	reportingStatusReasonViolations.forEach(v ->
            results.add(AviationAccountHelper.constructErrorMessageWithCA(accountId, accountDTO.getName(), emitter.getEmitterDisplayId(), emitter.getCa(),
                    v.getMessage(), v.getPropertyPath().iterator().next().getName() + ":" + v.getInvalidValue())));
        	locationViolations.forEach(v ->
            		results.add(AviationAccountHelper.constructErrorMessageWithCA(accountId, accountDTO.getName(), emitter.getEmitterDisplayId(), emitter.getCa(),
            				v.getMessage(), v.getPropertyPath().iterator().next().getName() + ":" + v.getInvalidValue())));
            failedCounter.incrementAndGet();
            return results;
        }
        
        try {
        	migrationAviationAccountCreationService.createMigratedAccount(accountDTO, locationDTO, emitter);

            results.add(AviationAccountHelper.constructSuccessMessage(accountId, accountDTO.getName(), emitter.getEmitterDisplayId(), emitter.getCa()));
        } catch (Exception ex) {
            failedCounter.incrementAndGet();
            log.error("migration of aviation account corsia: {} failed with {}",
                    emitter.getFldEmitterId(), ExceptionUtils.getRootCause(ex).getMessage());
            results.add(AviationAccountHelper.constructErrorMessageWithCA(accountId, accountDTO.getName(), emitter.getEmitterDisplayId(), emitter.getCa(),
                    ExceptionUtils.getRootCause(ex).getMessage(), null));
        }
        return results;
    }
}
