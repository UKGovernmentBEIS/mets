package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.AccountCreatedRegistryDetails;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AviationAccountCreatedRegistryDetails extends AccountCreatedRegistryDetails {

    private String monitoringPlanId;
    private Integer firstYearOfVerifiedEmissions;

}
