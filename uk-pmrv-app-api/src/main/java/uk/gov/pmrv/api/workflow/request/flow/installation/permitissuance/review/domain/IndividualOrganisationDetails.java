package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IndividualOrganisationDetails extends PermitIssuanceOrganizationDetails{
    private AddressDTO operatorAddress;
}
