package uk.gov.pmrv.api.account.installation.repo;


import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.account.service.AbstractAccountCustomRepositoryIT;

import java.time.LocalDate;

public class AccountCustomRepositoryImplIT extends AbstractAccountCustomRepositoryIT {

    @Override
    public Account buildAccount(Long id, String accountName, CompetentAuthorityEnum ca) {
        return InstallationAccount.builder()
            .id(id)
            .accountType(AccountType.INSTALLATION)
            .applicationType(ApplicationType.NEW_PERMIT)
            .commencementDate(LocalDate.now())
            .competentAuthority(ca)
            .verificationBodyId(id)
            .status(InstallationAccountStatus.NEW)
            .name(accountName)
            .siteName("account1")
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
            .emitterId("EM00001")
            .build();
    }
}
