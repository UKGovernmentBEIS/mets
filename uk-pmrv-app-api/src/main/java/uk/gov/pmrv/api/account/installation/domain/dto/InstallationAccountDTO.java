package uk.gov.pmrv.api.account.installation.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.domain.enumeration.SubsistenceCategory;


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@SpELExpression(expression = "{(#location != null)}", message = "installation.account.location.invalid")
@SpELExpression(expression = "{(#legalEntity != null)}", message = "installation.account.legalEntity.invalid")
@SpELExpression(expression = "{(#emissionTradingScheme eq 'UK_ETS_INSTALLATIONS') || (#emissionTradingScheme eq 'EU_ETS_INSTALLATIONS')}",
    message = "account.emissionTradingScheme.invalid")
public class InstallationAccountDTO extends AccountDTO {

    private InstallationAccountStatus status;

    @Size(max = 255)
    @NotBlank
    private String siteName;

    private EmitterType emitterType;

    private InstallationCategory installationCategory;

    private ApplicationType applicationType;

    private String transferCode;

    private SubsistenceCategory subsistenceCategory;

    private Boolean faStatus;
}
