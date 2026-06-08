package uk.gov.pmrv.api.verificationbody.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class VerificationBodyEmissionSchemeDTO {

    @NotNull
    private EmissionTradingScheme emissionTradingScheme;

    @NotBlank
    @Size(max=255, message = "{verificationBody.accreditationReferenceNumber.typeMismatch}")
    private String accreditationReferenceNumber;

    @Size(max=1000, message = "{verificationBody.accreditationName.typeMismatch}")
    private String accreditationName;

}
