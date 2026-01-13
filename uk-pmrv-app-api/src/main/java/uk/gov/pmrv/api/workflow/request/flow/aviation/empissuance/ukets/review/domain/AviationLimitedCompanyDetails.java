package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AviationLimitedCompanyDetails extends AviationOrganisationDetails {

    private String companyRegistrationNumber;
    private LocationDTO registeredAddress;

}
