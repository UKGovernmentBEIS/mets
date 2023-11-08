package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class IndividualOrganisation extends OrganisationStructure {

    @NotBlank
    @Size(max = 255)
    private String fullName;

}
