package uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEmissionSchemeDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationBodyDetails {

    private String name;

    private AddressDTO address;

    @Deprecated
    private String accreditationReferenceNumber;

    @Deprecated
    private Set<EmissionTradingScheme> emissionTradingSchemes;

    @Valid
    private Set<VerificationBodyEmissionSchemeDTO> verificationBodyEmissionSchemeDTOS;

    @AssertTrue(message = "Provide either verificationBodyEmissionSchemeDTOS OR (accreditationReferenceNumber and emissionTradingSchemes), but not both")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isValidCombination() {
        boolean hasNew = verificationBodyEmissionSchemeDTOS != null && !verificationBodyEmissionSchemeDTOS.isEmpty();

        boolean hasOld = emissionTradingSchemes != null && !emissionTradingSchemes.isEmpty()
                && accreditationReferenceNumber != null && !accreditationReferenceNumber.isBlank();

        boolean oldFieldsEmpty = (emissionTradingSchemes == null || emissionTradingSchemes.isEmpty()) &&
                        (accreditationReferenceNumber == null || accreditationReferenceNumber.isBlank());

        boolean newFieldEmpty = verificationBodyEmissionSchemeDTOS == null || verificationBodyEmissionSchemeDTOS.isEmpty();

        return (hasNew && oldFieldsEmpty) || (newFieldEmpty && hasOld);
    }
}
