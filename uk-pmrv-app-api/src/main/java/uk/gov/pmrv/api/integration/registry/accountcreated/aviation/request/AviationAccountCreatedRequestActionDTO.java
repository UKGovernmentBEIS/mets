package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.AccountCreatedRequestActionDTO;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AviationAccountCreatedRequestActionDTO extends AccountCreatedRequestActionDTO{

    private OrganisationStructure organisationStructure;
    private String operatorName;
    private LocalDate firstKnownAviationActivity;

}
