package uk.gov.pmrv.api.account.aviation.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountUpdateDTO;
import uk.gov.pmrv.api.account.domain.Location;
import uk.gov.pmrv.api.account.domain.LocationOnShoreState;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.service.validator.AccountStatus;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationAccountUpdateService {

    private final AviationAccountQueryService aviationAccountQueryService;
    private final AviationAccountStatusService aviationAccountStatusService;
    private final LocationMapper locationMapper;

    @Transactional
    @AccountStatus(expression = "{#status != 'CLOSED'}")
    public void updateAviationAccount(Long accountId, @Valid AviationAccountUpdateDTO aviationAccountUpdateDTO, AppUser user) {
        AviationAccount account = aviationAccountQueryService.getAccountById(accountId);
        validateAccountNameUniqueness(aviationAccountUpdateDTO.getName(), account.getCompetentAuthority(), account.getEmissionTradingScheme(), account.getId());
        validateCrcoCodeUniqueness(aviationAccountUpdateDTO.getCrcoCode(), account.getCompetentAuthority(), account.getEmissionTradingScheme(), account.getId());
        validateEmissionTradingSchemeRegistryId(account, aviationAccountUpdateDTO.getRegistryId());

        account.setName(aviationAccountUpdateDTO.getName());
        account.setRegistryId(aviationAccountUpdateDTO.getRegistryId());
        account.setSopId(aviationAccountUpdateDTO.getSopId());
        account.setCrcoCode(aviationAccountUpdateDTO.getCrcoCode());
        account.setCommencementDate(aviationAccountUpdateDTO.getCommencementDate());

        // Set location
        if(aviationAccountUpdateDTO.getLocation() != null) {
            LocationOnShoreState locationOnShoreState = (LocationOnShoreState) locationMapper
                    .toLocation(aviationAccountUpdateDTO.getLocation());

            Location accountLocation = account.getLocation() instanceof HibernateProxy
                    ? (Location) Hibernate.unproxy(account.getLocation())
                    : account.getLocation();

            Optional.ofNullable(accountLocation).ifPresentOrElse(loc -> {
                LocationOnShoreState location = (LocationOnShoreState) loc;

                location.getAddress().setLine1(locationOnShoreState.getAddress().getLine1());
                location.getAddress().setLine2(locationOnShoreState.getAddress().getLine2());
                location.getAddress().setCity(locationOnShoreState.getAddress().getCity());
                location.getAddress().setCountry(locationOnShoreState.getAddress().getCountry());
                location.getAddress().setPostcode(locationOnShoreState.getAddress().getPostcode());
                location.getAddress().setState(locationOnShoreState.getAddress().getState());
            }, () -> account.setLocation(locationOnShoreState));
        } else {
            account.setLocation(null);
        }
        account.setUpdatedBy(user.getUserId());
        account.setLastUpdatedDate(LocalDateTime.now());
    }

    @Transactional
    public void updateAccountUponEmpApproved(Long accountId, String name, LocationOnShoreStateDTO accountContactLocationDTO) {
        updateNameAndLocation(accountId, name, accountContactLocationDTO);

        aviationAccountStatusService.handleEmpApproved(accountId);
    }

    @Transactional
    @AccountStatus(expression = "{#status == 'LIVE'}")
    public void updateAccountUponEmpVariationApproved(Long accountId, String name, LocationOnShoreStateDTO accountContactLocationDTO) {
        updateNameAndLocation(accountId, name, accountContactLocationDTO);
    }

	private void updateNameAndLocation(Long accountId, String name, LocationOnShoreStateDTO accountContactLocationDTO) {
		AviationAccount account = aviationAccountQueryService.getAccountById(accountId);
        validateAccountNameUniqueness(name, account.getCompetentAuthority(), account.getEmissionTradingScheme(), account.getId());

        account.setName(name);
        account.setLocation(locationMapper.toLocation(accountContactLocationDTO));
	}

	private void validateAccountNameUniqueness(String name, CompetentAuthorityEnum competentAuthority,
                                               EmissionTradingScheme emissionTradingScheme, Long accountId) {
        if (aviationAccountQueryService.isExistingAccountName(name, competentAuthority, emissionTradingScheme, accountId)) {
            throw new BusinessException(MetsErrorCode.ACCOUNT_REGISTRATION_NUMBER_ALREADY_EXISTS, name, competentAuthority, emissionTradingScheme);
        }
    }

    private void validateCrcoCodeUniqueness(String crcoCode, CompetentAuthorityEnum competentAuthority,
                                            EmissionTradingScheme emissionTradingScheme, Long accountId) {
        if (aviationAccountQueryService.isExistingCrcoCode(crcoCode, competentAuthority, emissionTradingScheme, accountId)) {
            throw new BusinessException(MetsErrorCode.CRCO_CODE_ALREADY_RELATED_WITH_ANOTHER_ACCOUNT,
                    crcoCode, competentAuthority, emissionTradingScheme);
        }
    }

    private void validateEmissionTradingSchemeRegistryId(AviationAccount account, Integer registryId) {
        if (!EmissionTradingScheme.UK_ETS_AVIATION.equals(account.getEmissionTradingScheme()) && registryId != null) {
            throw new BusinessException(MetsErrorCode.REGISTRY_ID_SUBMITTED_ONLY_FOR_UK_ETS_AVIATION_ACCOUNTS,
                    account.getName(), account.getRegistryId(), account.getCompetentAuthority(), account.getEmissionTradingScheme());
        }
    }

    @Transactional
	public void closeAviationAccount(Long accountId, AppUser user, String reason) {
		AviationAccount account = aviationAccountQueryService.getAccountById(accountId);
        account.setClosureReason(reason);
        account.setClosingDate(LocalDateTime.now());
        account.setClosedBy(user.getUserId());
        account.setClosedByName(user.getFullName());

        aviationAccountStatusService.handleCloseAccount(accountId);
		
	}

}
