package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#emissionTradingScheme eq 'UK_ETS_INSTALLATIONS') || (#emissionTradingScheme eq 'EU_ETS_INSTALLATIONS')}",
    message = "account.emissionTradingScheme.invalid")
public class InstallationAccountPayload {

    @NotNull(message = "{account.type.notEmpty}")
    private AccountType accountType;

    @NotNull(message = "{application.type.notEmpty}")
    private ApplicationType applicationType;

    @NotBlank(message = "{account.name.notEmpty}")
    @Size(max = 255, message = "{account.name.typeMismatch}")
    private String name;

    private InstallationAccountSubmitter submitter;

    @NotBlank(message = "{account.siteName.notEmpty}")
    @Size(max = 255, message = "{account.siteName.invalidSize}")
    private String siteName;
    
    @NotNull(message = "{account.emissionTradingScheme.notEmpty}")
    private EmissionTradingScheme emissionTradingScheme;

    @NotNull(message = "{account.competentAuthority.notEmpty}")
    private CompetentAuthorityEnum competentAuthority;

    @NotNull(message = "{account.commencementDate.notEmpty}")
    private LocalDate commencementDate;

    @NotNull(message = "{account.legalEntity.notEmpty}")
    @Valid
    private LegalEntityDTO legalEntity;

    @NotNull(message = "{account.location.notEmpty}")
    @Valid
    private LocationDTO location;
}
