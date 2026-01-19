package uk.gov.pmrv.api.account.aviation.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountUpdateDTO;
import uk.gov.pmrv.api.account.domain.Location;
import uk.gov.pmrv.api.account.domain.LocationOnShoreState;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.service.validator.AccountStatus;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AviationAccountUpdateService {

    private final AviationAccountQueryService aviationAccountQueryService;
    private final AviationAccountStatusService aviationAccountStatusService;
    private final LocationMapper locationMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @AccountStatus(expression = "{#status != 'CLOSED'}")
    public void updateAviationAccount(Long accountId, @Valid AviationAccountUpdateDTO aviationAccountUpdateDTO, AppUser user) {
        AviationAccount account = aviationAccountQueryService.getAccountById(accountId);
        validateAccountNameUniqueness(aviationAccountUpdateDTO.getName(), account.getCompetentAuthority(), account.getEmissionTradingScheme(), account.getId());
        validateCrcoCodeUniqueness(aviationAccountUpdateDTO.getCrcoCode(), account.getCompetentAuthority(), account.getEmissionTradingScheme(), account.getId());

        account.setName(aviationAccountUpdateDTO.getName());
        account.setSopId(aviationAccountUpdateDTO.getSopId());
        account.setCrcoCode(aviationAccountUpdateDTO.getCrcoCode());

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

    @Transactional
    public void closeAviationAccount(Long accountId, AppUser user, String reason) {
        AviationAccount account = aviationAccountQueryService.getAccountById(accountId);
        account.setClosureReason(reason);
        account.setClosingDate(LocalDateTime.now());
        account.setClosedBy(user.getUserId());
        account.setClosedByName(user.getFullName());

        aviationAccountStatusService.handleCloseAccount(accountId);

    }

    @Transactional
    public void updateAccountCommencementDate(Long accountId, LocalDate commencementDate) {
        AviationAccount account = aviationAccountQueryService.getAccountById(accountId);
        account.setCommencementDate(commencementDate);
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

}
