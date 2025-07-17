package uk.gov.pmrv.api.account.installation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityWithoutHoldingCompanyDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallationAccountWithoutLeHoldingCompanyDTO {

    private String name;
    private String siteName;
    private CompetentAuthorityEnum competentAuthority;
    private AccountType accountType;
    private EmitterType emitterType;
    private InstallationCategory installationCategory;
    private LocationDTO location;
    private LegalEntityWithoutHoldingCompanyDTO legalEntity;
    private String emitterId;
    private Boolean faStatus;
}
