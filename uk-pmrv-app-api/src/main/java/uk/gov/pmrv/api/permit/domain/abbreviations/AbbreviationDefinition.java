package uk.gov.pmrv.api.permit.domain.abbreviations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbbreviationDefinition {

    @NotBlank
    @Size(max=10000)
    private String abbreviation;

    @NotBlank
    @Size(max=10000)
    private String definition;
}
