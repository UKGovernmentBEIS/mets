package uk.gov.pmrv.api.migration.aviationaccount.common;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountMapper;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.MigrationHelper;

import java.util.List;
import java.util.Optional;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

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
		AppUser authUser = buildAuthUser(Optional.ofNullable(
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
			AppUser authUser, Long identifier) {
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
	
	private AppUser buildAuthUser(CompetentAuthorityEnum ca) {
        AppUser authUser = new AppUser();
        authUser.setFirstName("Migration");
        authUser.setLastName("user");
        authUser.setUserId("Migration user");
        authUser.setRoleType(RoleTypeConstants.REGULATOR);
        authUser.setAuthorities(
            List.of(AppAuthority.builder().competentAuthority(ca).build()));
        return authUser;
    }
	
	private void validateAccountNameUniqueness(String name, CompetentAuthorityEnum competentAuthority,
            EmissionTradingScheme emissionTradingScheme) {
				if(aviationAccountQueryService.isExistingAccountName(name, competentAuthority, emissionTradingScheme)) {
					throw new BusinessException(
							MetsErrorCode.ACCOUNT_REGISTRATION_NUMBER_ALREADY_EXISTS, name, competentAuthority, emissionTradingScheme);
				}
	}

	private void validateCrcoCodeUniqueness(String crcoCode, CompetentAuthorityEnum competentAuthority,
			EmissionTradingScheme emissionTradingScheme) {
				if(aviationAccountQueryService.isExistingCrcoCode(
						crcoCode, competentAuthority, emissionTradingScheme)) {
					throw new BusinessException(MetsErrorCode.CRCO_CODE_ALREADY_RELATED_WITH_ANOTHER_ACCOUNT,
							crcoCode, competentAuthority, emissionTradingScheme);
				}
	}
}
