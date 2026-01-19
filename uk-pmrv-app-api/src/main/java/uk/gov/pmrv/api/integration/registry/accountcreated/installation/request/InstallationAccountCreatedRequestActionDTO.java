package uk.gov.pmrv.api.integration.registry.accountcreated.installation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.AccountCreatedRequestActionDTO;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InstallationAccountCreatedRequestActionDTO extends AccountCreatedRequestActionDTO {

    private String installationName;
    private LegalEntityDTO legalEntityDTO;
    private Integer registryReportingFirstYear;
    private List<RegulatedActivityType> regulatedActivityList;


}
