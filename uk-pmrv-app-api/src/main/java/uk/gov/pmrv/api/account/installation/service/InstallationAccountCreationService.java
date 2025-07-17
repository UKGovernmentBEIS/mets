package uk.gov.pmrv.api.account.installation.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.transform.InstallationAccountMapper;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.AccountIdentifierService;
import uk.gov.pmrv.api.account.service.LegalEntityService;

@Validated
@Service
@RequiredArgsConstructor
public class InstallationAccountCreationService {

    private final AccountRepository accountRepository;
    private final AccountIdentifierService accountIdentifierService;
    private final LegalEntityService legalEntityService;
    private final InstallationAccountQueryService installationAccountQueryService;
    private final InstallationAccountMapper installationAccountMapper;

    @Transactional
    public InstallationAccountDTO createAccount(@Valid InstallationAccountDTO accountDTO, AppUser appUser) {
        installationAccountQueryService.validateAccountNameExistence(accountDTO.getName());

        final LegalEntity legalEntity = legalEntityService.resolveLegalEntity(accountDTO.getLegalEntity(), appUser);
        final Long identifier = accountDTO.getId() != null ? accountDTO.getId()
                : accountIdentifierService.incrementAndGet();

        // Create account
        InstallationAccount account = installationAccountMapper.toInstallationAccount(accountDTO, identifier);
        account.setStatus(InstallationAccountStatus.UNAPPROVED);
        account.setLegalEntity(legalEntity);
        InstallationAccount persistedAccount = accountRepository.save(account);

        return installationAccountMapper.toInstallationAccountDTO(persistedAccount);
    }
    
}
