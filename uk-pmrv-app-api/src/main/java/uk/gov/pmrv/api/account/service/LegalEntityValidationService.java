package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

@Service
@RequiredArgsConstructor
public class LegalEntityValidationService {

    private final LegalEntityRepository legalEntityRepository;

    public void validateNameExistenceInOtherActiveLegalEntities(String name, Long legalEntityId) {
        if(legalEntityRepository.existsByNameAndStatusAndIdNot(name, LegalEntityStatus.ACTIVE, legalEntityId)) {
            throw new BusinessException(MetsErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);
        }
    }

    public boolean isExistingActiveLegalEntityName(String leName) {
        return isExistingActiveLegalEntityName(leName, null);
    }

    /**
     * Checks if an active legal entity exists for the provided user.
     *
     * @param leName Legal entity name
     * @param appUser {@link AppUser}
     * @return True if an active legal entity exists
     */
    public boolean isExistingActiveLegalEntityName(String leName, AppUser appUser) {
        if(appUser == null) {
            return legalEntityRepository.existsByNameAndStatus(leName, LegalEntityStatus.ACTIVE);
        }

        switch (appUser.getRoleType()) {
            case RoleTypeConstants.REGULATOR:
                return legalEntityRepository.existsByNameAndStatus(leName, LegalEntityStatus.ACTIVE);
            case RoleTypeConstants.OPERATOR:
                return legalEntityRepository.existsActiveLegalEntityNameInAnyOfAccounts(leName, appUser.getAccounts());
            default:
                throw new BusinessException(MetsErrorCode.LEGAL_ENTITY_NOT_ASSOCIATED_WITH_USER);
        }
    }
}
