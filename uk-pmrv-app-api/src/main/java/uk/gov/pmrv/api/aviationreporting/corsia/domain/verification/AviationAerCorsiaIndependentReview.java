package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.referencedata.service.Country;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerCorsiaIndependentReview {

    @NotBlank
    @Size(max = 10000)
    private String reviewResults;

    @NotBlank
    @Size(max = 500)
    private String name;

    @NotBlank
    @Size(max = 500)
    private String position;

    @Email
    @Size(max = 255)
    @NotBlank
    private String email;

    @NotBlank(message = "{address.line1.notEmpty}")
    @Size(max = 255, message = "{address.line1.typeMismatch}")
    private String line1;

    @Size(max = 255, message = "{address.line2.typeMismatch}")
    private String line2;


    @NotBlank(message = "{address.city.notEmpty}")
    @Size(max = 255, message = "{address.city.typeMismatch}")
    private String city;

    @NotBlank(message = "{address.country.notEmpty}")
    @Country(message = "{address.country.typeMismatch}")
    private String country;

    @Size(max=64, message = "{address.postcode.typeMismatch}")
    private String postcode;

    @Size(max = 255)
    private String state;

}
