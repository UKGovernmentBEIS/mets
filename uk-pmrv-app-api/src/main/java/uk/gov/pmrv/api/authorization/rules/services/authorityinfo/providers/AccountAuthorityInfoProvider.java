package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers;

import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

import java.util.Optional;

public interface AccountAuthorityInfoProvider {
    CompetentAuthorityEnum getAccountCa(Long accountId);
    Optional<Long> getAccountVerificationBodyId(Long accountId);
}
