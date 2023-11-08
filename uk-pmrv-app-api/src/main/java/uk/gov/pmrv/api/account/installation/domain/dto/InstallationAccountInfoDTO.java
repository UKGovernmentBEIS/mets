package uk.gov.pmrv.api.account.installation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstallationAccountInfoDTO {

    private Long id;
    private String name;
    private AccountType accountType;
    private InstallationAccountStatus status;
    private CompetentAuthorityEnum competentAuthority;
    private EmitterType emitterType;
    private InstallationCategory installationCategory;
    private String transferCode;
    private Boolean faStatus;
}
