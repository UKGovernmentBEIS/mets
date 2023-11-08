package uk.gov.pmrv.api.account.installation.repo;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.repo.AbstractLegalEntityRepositoryIT;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.time.LocalDate;

public class LegalEntityRepositoryIT extends AbstractLegalEntityRepositoryIT {
    @Override
    public Account buildAccount(Long id, String accountName, LegalEntity legalEntity) {
        return InstallationAccount.builder()
            .id(id)
            .accountType(AccountType.INSTALLATION)
            .applicationType(ApplicationType.NEW_PERMIT)
            .status(InstallationAccountStatus.LIVE)
            .commencementDate(LocalDate.now())
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .location(
                LocationOnShore.builder()
                    .gridReference("grid")
                    .address(
                        Address.builder()
                            .city("city")
                            .country("GR")
                            .line1("line")
                            .postcode("postcode")
                            .build())
                    .build())
            .name(accountName)
            .siteName(accountName)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
            .emitterId("EM0000" + id.toString())
            .legalEntity(legalEntity)
            .build();
    }
}
