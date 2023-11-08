package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpAbbreviationDefinition {

    @NotBlank
    @Size(max=50)
    private String abbreviation;

    @NotBlank
    @Size(max=500)
    private String definition;
}
