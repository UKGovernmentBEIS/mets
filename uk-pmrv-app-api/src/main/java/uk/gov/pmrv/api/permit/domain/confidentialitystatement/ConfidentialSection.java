package uk.gov.pmrv.api.permit.domain.confidentialitystatement;

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
public class ConfidentialSection {

    @NotBlank
    @Size(max=10000)
    private String section;

    @NotBlank
    @Size(max=10000)
    private String explanation;
}
