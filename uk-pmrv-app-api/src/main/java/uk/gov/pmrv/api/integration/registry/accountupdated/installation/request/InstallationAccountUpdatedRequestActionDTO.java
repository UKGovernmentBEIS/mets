package uk.gov.pmrv.api.integration.registry.accountupdated.installation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationAccountUpdatedRequestActionDTO {

    PermitContainer permitContainer;
    InstallationAccountPermitDTO installationAccount;

}
