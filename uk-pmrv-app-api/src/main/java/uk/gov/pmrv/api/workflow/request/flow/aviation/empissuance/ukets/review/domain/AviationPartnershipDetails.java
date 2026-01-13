package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AviationPartnershipDetails extends AviationOrganisationDetails {

    private String partnershipName;
    private LocationDTO mainOfficeAddress;

}
