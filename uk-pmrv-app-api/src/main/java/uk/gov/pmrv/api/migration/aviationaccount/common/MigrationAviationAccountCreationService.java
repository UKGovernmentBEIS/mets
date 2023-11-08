package uk.gov.pmrv.api.migration.aviationaccount.common;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountMapper;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.MigrationHelper;

@Service
@AllArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MigrationAviationAccountCreationService {

    private final LocationMapper locationMapper;
    private final AviationAccountRepository aviationAccountRepository;
    private final AviationAccountQueryService aviationAccountQueryService;
    private final AviationAccountMapper aviationAccountMapper;
    
	@Transactional
    public void createMigratedAccount(
    		AviationAccountCreationDTO accountDTO, LocationOnShoreStateDTO locationDTO, 
            AviationEmitter emitter) throws Exception {
		
		Long identifier = Long.parseLong(emitter.getEmitterDisplayId());
		PmrvUser authUser = buildAuthUser(Optional.ofNullable(
    			MigrationHelper.resolveCompAuth(emitter.getCa()))
    			.orElseThrow(() -> new Exception(String.format(
    					"'%s' - CA cannot be resolved for ETSWAP value '%s'", emitter.getFldEmitterId(), emitter.getCa()))));
		
        createNewAccount(accountDTO, emitter, authUser, identifier);
        
        // update status and add location if status is LIVE
        AviationAccount newAccount = aviationAccountRepository.findById(identifier)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        if ("LIVE".equalsIgnoreCase(emitter.getEmitterStatus())) {
        	newAccount.setLocation(locationMapper.toLocation(locationDTO));
        	newAccount.setStatus(AviationAccountStatus.LIVE);
    	}
    	// set submission date anew, since it is the date the status entry is first created 
        // but for migrated accounts we need the date originally stored in ETSWAP
        AviationAccountReportingStatusHistory entry = newAccount.getReportingStatusHistoryList().get(0);
        entry.setSubmissionDate(emitter.getReportingStatusSubmissionDate());
    }

	private void createNewAccount(AviationAccountCreationDTO accountDTO, AviationEmitter emitter,
			PmrvUser authUser, Long identifier) {
		EmissionTradingScheme emissionTradingScheme = accountDTO.getEmissionTradingScheme();
        CompetentAuthorityEnum competentAuthority = authUser.getCompetentAuthority();

        validateAccountNameUniqueness(accountDTO.getName(), competentAuthority, emissionTradingScheme);
        validateCrcoCodeUniqueness(accountDTO.getCrcoCode(), competentAuthority, emissionTradingScheme);
        
		AviationAccount account = aviationAccountMapper.toAviationAccount(accountDTO, competentAuthority, identifier);
        account.setAcceptedDate(emitter.getFldDateCreated());
        account.setMigratedAccountId(emitter.getFldEmitterId());
        account.setRegistryId(emitter.getFldRegistration());
        
        final AviationAccountReportingStatus reporingStatus = AviationAccountReportingStatus.valueOf(emitter.getReportingStatus());
        account.setReportingStatus(reporingStatus);
        account.addReportingStatusHistory(AviationAccountReportingStatusHistory.builder()
                .status(reporingStatus)
                .reason(emitter.getReportingStatusReason())
                .submitterId(authUser.getUserId())
                .submitterName(authUser.getFullName())
                .build());
        
        aviationAccountRepository.saveAndFlush(account);
	}
	
	private PmrvUser buildAuthUser(CompetentAuthorityEnum ca) {
        PmrvUser authUser = new PmrvUser();
        authUser.setFirstName("Migration");
        authUser.setLastName("user");
        authUser.setUserId("Migration user");
        authUser.setRoleType(RoleType.REGULATOR);
        authUser.setAuthorities(
            List.of(PmrvAuthority.builder().competentAuthority(ca).build()));
        return authUser;
    }
	
	private void validateAccountNameUniqueness(String name, CompetentAuthorityEnum competentAuthority,
            EmissionTradingScheme emissionTradingScheme) {
				if(aviationAccountQueryService.isExistingAccountName(name, competentAuthority, emissionTradingScheme)) {
					throw new BusinessException(
							ErrorCode.ACCOUNT_ALREADY_EXISTS, name, competentAuthority, emissionTradingScheme);
				}
	}

	private void validateCrcoCodeUniqueness(String crcoCode, CompetentAuthorityEnum competentAuthority,
			EmissionTradingScheme emissionTradingScheme) {
				if(aviationAccountQueryService.isExistingCrcoCode(
						crcoCode, competentAuthority, emissionTradingScheme)) {
					throw new BusinessException(ErrorCode.CRCO_CODE_ALREADY_RELATED_WITH_ANOTHER_ACCOUNT,
							crcoCode, competentAuthority, emissionTradingScheme);
				}
	}
}
