package uk.gov.pmrv.api.web.orchestrator.account.installation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.web.orchestrator.account.AccountHeaderInfoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class InstallationAccountHeaderInfoDTO extends AccountHeaderInfoDTO {

    private InstallationAccountStatus status;
    private EmitterType emitterType;
    private InstallationCategory installationCategory;
    private String permitId;
}
