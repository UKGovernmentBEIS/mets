package uk.gov.pmrv.api.migration.installationaccount;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.TransferCodeStatus;
import uk.gov.pmrv.api.account.installation.repository.InstallationAccountRepository;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.MigrationHelper;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
class MigrationInstallationAccountCreationService {

    private final uk.gov.pmrv.api.account.installation.service.InstallationAccountCreationService installationAccountCreationService;
    private final InstallationAccountRepository installationAccountRepository;

    @Transactional
    public Long createAccount(InstallationAccountDTO accountDTO, Emitter emitter) throws Exception {
        AppUser authUser = buildAuthUser(accountDTO.getCompetentAuthority());

        InstallationAccountDTO createdAccountDTO = installationAccountCreationService.createAccount(accountDTO, authUser);
        
        Optional<InstallationAccount> accountOpt = installationAccountRepository.findById(createdAccountDTO.getId());
        if(accountOpt.isEmpty()) {
            throw new Exception(String.format("Account for emitter id %s failed to persist in PMRV", emitter.getId()));
        }

        InstallationAccount account = accountOpt.get();
        account.setMigratedAccountId(emitter.getId());
        populateStatus(account, emitter.getStatus());

        final boolean isTransfer = accountDTO.getApplicationType() == ApplicationType.TRANSFER;
        account.setTransferCodeStatus(isTransfer ? TransferCodeStatus.DISABLED : null);
        
        installationAccountRepository.save(account);
        return account.getId();
    }

    private void populateStatus(InstallationAccount account, String etsStatus) {
        //TODO: Correlate all ETSWAP Installation Account status values with the PMRV ones
        InstallationAccountStatus accountStatus = MigrationHelper.resolveInstallationAccountStatus(etsStatus);
        if(accountStatus != null) {
            account.setStatus(accountStatus);
        }
    }

    private AppUser buildAuthUser(CompetentAuthorityEnum ca) {
        AppUser authUser = new AppUser();
        authUser.setRoleType(RoleTypeConstants.REGULATOR);
        authUser.setAuthorities(
            List.of(AppAuthority.builder().competentAuthority(ca).build()));
        return authUser;
    }

}
