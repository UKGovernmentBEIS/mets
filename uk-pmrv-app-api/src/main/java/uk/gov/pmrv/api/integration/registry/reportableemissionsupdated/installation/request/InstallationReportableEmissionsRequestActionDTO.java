package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.AccountCreatedRequestActionDTO;

import java.time.Year;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InstallationReportableEmissionsRequestActionDTO extends AccountCreatedRequestActionDTO {

    private String installationName;
    private LegalEntityDTO legalEntityDTO;
    private Integer registryId;
    private String reportableEmissions;
    private Year reportingYear;

}
