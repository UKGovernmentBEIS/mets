package uk.gov.pmrv.api.account.installation.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.transform.InstallationAccountMapper;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.LegalEntityService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Validated
@Service
@RequiredArgsConstructor
public class InstallationAccountAmendService {

    private final AccountRepository accountRepository;
    private final LegalEntityService legalEntityService;
    private final InstallationAccountQueryService installationAccountQueryService;
    private final InstallationAccountMapper installationAccountMapper;

    @Transactional
    public InstallationAccountDTO amendAccount(Long accountId, @Valid InstallationAccountDTO previousAccountDTO,
                                               @Valid InstallationAccountDTO newAccountDTO, AppUser appUser) {
        validateNonAmendableAccountFields(previousAccountDTO, newAccountDTO);
        validateAccountName(previousAccountDTO, newAccountDTO);

        InstallationAccount account = installationAccountQueryService.getAccountFullInfoById(accountId);

        LegalEntity legalEntity = legalEntityService.getLegalEntityById(account.getLegalEntity().getId());

        final LegalEntity newLegalEntity = 
                legalEntityService.resolveAmendedLegalEntity(newAccountDTO.getLegalEntity(), legalEntity, appUser);

        //update account
        InstallationAccount newAccount = installationAccountMapper.toInstallationAccount(newAccountDTO, account.getId());
        newAccount.setStatus(account.getStatus());
        newAccount.getLocation().setId(account.getLocation().getId());
        newAccount.setLegalEntity(newLegalEntity);
        InstallationAccount persistedAccount = accountRepository.save(newAccount);

        //delete current LE if not used anymore by any account
        if (installationAccountQueryService.isLegalEntityUnused(legalEntity.getId())) {
            legalEntityService.deleteLegalEntity(legalEntity);
        }
        
        return installationAccountMapper.toInstallationAccountDTO(persistedAccount);
    }

    private void validateNonAmendableAccountFields(InstallationAccountDTO previousAccountDTO, InstallationAccountDTO newAccountDTO) {
        List<String> errors = new ArrayList<>();

        if (!previousAccountDTO.getCommencementDate().equals(newAccountDTO.getCommencementDate())) {
            errors.add("commencementDate");
        }

        if (!previousAccountDTO.getCompetentAuthority().equals(newAccountDTO.getCompetentAuthority())) {
            errors.add("competentAuthority");
        }

        if (!previousAccountDTO.getLocation().getType().equals(newAccountDTO.getLocation().getType())) {
            errors.add("location.type");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(MetsErrorCode.ACCOUNT_FIELD_NOT_AMENDABLE, errors.toArray());
        }
    }

    private void validateAccountName(InstallationAccountDTO previousAccountDTO, InstallationAccountDTO newAccountDTO) {
        if (!Objects.equals(previousAccountDTO.getName(), newAccountDTO.getName())) {
            installationAccountQueryService.validateAccountNameExistence(newAccountDTO.getName());
        }
    }
}
