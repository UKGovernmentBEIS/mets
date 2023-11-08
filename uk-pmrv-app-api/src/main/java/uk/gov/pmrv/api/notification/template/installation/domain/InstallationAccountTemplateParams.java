package uk.gov.pmrv.api.notification.template.installation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.AccountTemplateParams;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InstallationAccountTemplateParams extends AccountTemplateParams {

    private String siteName;
    private String emitterType;

    private String legalEntityName;
    private String legalEntityLocation;

    private String installationCategory;
}
