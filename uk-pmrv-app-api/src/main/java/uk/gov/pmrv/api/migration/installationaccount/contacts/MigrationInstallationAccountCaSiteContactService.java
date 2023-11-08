package uk.gov.pmrv.api.migration.installationaccount.contacts;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.installationaccount.InstallationAccountHelper;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationInstallationAccountCaSiteContactService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final UserAuthService userAuthService;
    private final AccountRepository accountRepository;
    private final AuthorityRepository authorityRepository;

    private static final String QUERY_BASE = """
        select e.fldEmitterId AS account_id,
               e.fldCompetentAuthorityUserID AS ca_contact_id,
               e.fldName AS name,
               e.fldEmitterDisplayId AS emitter_display_id,
               ca.fldEmailAddress AS ca_contact_email
          from tblEmitter e
          join tlkpEmitterType et on e.fldEmitterTypeID = et.fldEmitterTypeID
          join tlkpEmitterStatus es on e.fldEmitterStatusID = es.fldEmitterStatusID and (es.fldDisplayName = 'Live' or e.fldEmitterID in (select fldEmitterID from mig_emitters_explicitly_live))
     left join tblUser caUser on e.fldCompetentAuthorityUserID = caUser.fldUserID
          join tblContact ca on caUser.fldContactID = ca.fldContactID
         where et.fldName = 'Installation'
           and caUser.fldIsDisabled = 0
     """;

    @Override
    public String getResource() {
        return "installation-accounts-ca-site-contact";
    }

    @Override
    public List<String> migrate(String ids) {
        final String query = InstallationAccountHelper.constructQuery(QUERY_BASE, ids);
        List<String> failed = new ArrayList<>();
        List<CaSiteContact> caSiteContacts = migrationJdbcTemplate.query(query, new CaSiteContactsMapper());
        for (CaSiteContact caSiteContact: caSiteContacts) {
            try {
                failed.addAll(migrateInstallationAccountCaSiteContact(caSiteContact));
            } catch (Exception ex) {
                log.error("migration of installation account ca site contact: {} failed with {}",
                        caSiteContact.getEmitterId(), ExceptionUtils.getRootCause(ex).getMessage());
                failed.add("Id: " + caSiteContact.getEmitterId() + " | Error: " + ExceptionUtils.getRootCause(ex).getMessage());
            }
        }
        return failed;
    }

    private List<String> migrateInstallationAccountCaSiteContact(CaSiteContact caSiteContact) {
        List<String> failedEntries = new ArrayList<>();
        String accountId = caSiteContact.getEmitterId();

        Account migratedAccount = accountRepository.findByMigratedAccount(accountId);

        if (migratedAccount == null) {
            failedEntries.add(InstallationAccountHelper.constructErrorMessage(accountId, caSiteContact.getEmitterName(), caSiteContact.getEmitterDisplayId(),
                    "account with migrated id does not exist", accountId));
        } else {
            String etswapCaContactEmail = caSiteContact.getCaContactEmail();

            if(etswapCaContactEmail != null) {
                userAuthService.getUserByEmail(etswapCaContactEmail).ifPresentOrElse(
                    userInfo -> {
                        String accountCaSiteContactUserId = userInfo.getUserId();
                        //validate user's capability to be assigned as ca site contact to account
                        failedEntries.addAll(validateAccountCaSiteContactUser(userInfo.getUserId(), migratedAccount, caSiteContact.getEmitterDisplayId()));

                        //assign user as ca site contact to account
                        if (failedEntries.isEmpty()) {
                            doAssignCaSiteContact(accountCaSiteContactUserId, migratedAccount);
                        }
                    },
                    //if user's email not found in keycloak then fail
                    () -> failedEntries.add(InstallationAccountHelper.constructErrorMessage(migratedAccount.getMigratedAccountId(), caSiteContact.getEmitterName(), caSiteContact.getEmitterDisplayId(),
                        "User with email not found in keycloak", etswapCaContactEmail))
                );
            } else {
                //if no ca contact found in etswap then fail
                failedEntries.add(InstallationAccountHelper.constructErrorMessage(migratedAccount.getMigratedAccountId(), caSiteContact.getEmitterName(), caSiteContact.getEmitterDisplayId(),
                        "CA site contact user not found in ETSWAP", ""));
            }
        }

        return failedEntries;
    }


    /**
     * During migration of account contacts, the following validations are performed :
     * 1.Candidate user to be assigned as ca site account contact is related to the competent authority of the account
     *
     * Main difference with {@link uk.gov.pmrv.api.account.service.AccountCaSiteContactService#updateCaSiteContacts(PmrvUser, List)}
     * is that during migration we do not check that candidate users have active authorities on the competent authority of the account.
     * Also we don't check if the account to which we assign the ca site contact is ACTIVE.
     */
    private List<String> validateAccountCaSiteContactUser(String userId, Account migratedAccount, String emitterDisplayId) {
        List<String> failedEntries = new ArrayList<>();

        CompetentAuthorityEnum migratedAccountCa = migratedAccount.getCompetentAuthority();

        Optional<Authority> optionalUserAuthority = authorityRepository.findByUserIdAndCompetentAuthority(userId, migratedAccountCa);

        if (optionalUserAuthority.isEmpty()) {
            failedEntries.add(InstallationAccountHelper.constructErrorMessage(migratedAccount.getMigratedAccountId(), migratedAccount.getName(), emitterDisplayId,
                "User is not related with ca " + migratedAccountCa, userId));
        }

        return failedEntries;
    }

    private void doAssignCaSiteContact(String userId, Account migratedAccount) {
        migratedAccount.getContacts().put(AccountContactType.CA_SITE, userId);
        accountRepository.save(migratedAccount);
    }
}
