package uk.gov.pmrv.api.account.installation.repo;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.HoldingCompany;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.repo.AbstractAccountRepositoryIT;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.time.LocalDate;

public class AccountRepositoryIT extends AbstractAccountRepositoryIT {
    @Override
    public Account buildAccount(Long id, String accountName, CompetentAuthorityEnum ca, Long verificationBodyId,
                                EmissionTradingScheme emissionTradingScheme, LegalEntity legalEntity, HoldingCompany holdingCompany) {
        return InstallationAccount.builder()
            .id(id)
            .legalEntity(legalEntity)
            .accountType(getAccounTtype())
            .applicationType(ApplicationType.NEW_PERMIT)
            .commencementDate(LocalDate.now())
            .competentAuthority(ca)
            .verificationBodyId(verificationBodyId)
            .status(InstallationAccountStatus.LIVE)
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
            .emissionTradingScheme(emissionTradingScheme)
            .emitterId("EM" + String.format("%05d", id))
            .build();
    }

    @Override
    public AccountType getAccounTtype() {
        return AccountType.INSTALLATION;
    }
}
