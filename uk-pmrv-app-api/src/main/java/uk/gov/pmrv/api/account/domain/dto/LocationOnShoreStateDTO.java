package uk.gov.pmrv.api.account.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.referencedata.service.Country;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LocationOnShoreStateDTO extends LocationDTO {

    @NotBlank(message = "{address.line1.notEmpty}")
    @Size(max = 100, message = "{address.line1.typeMismatch}")
    private String line1;

    @Size(max = 100, message = "{address.line2.typeMismatch}")
    private String line2;

    @NotBlank(message = "{address.city.notEmpty}")
    @Size(max = 100, message = "{address.city.typeMismatch}")
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 20, message = "{address.postcode.typeMismatch}")
    private String postcode;

    @NotBlank(message = "{address.country.notEmpty}")
    @Country
    private String country;
}
