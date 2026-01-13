package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessOrganisationDetails extends RegistryIntegrationOrganizationDetails {

    private AddressDTO registeredAddress;
    private String companyRegistrationNumber;
    private String justification;
}
