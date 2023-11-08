package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{(#accreditationReferenceDocumentTypes?.contains('OTHER')) == (#otherReference != null)}", message = "aviationAerVerificationData.materialityLevel.otherReference")
public class AviationAerMaterialityLevel {

    @NotBlank
    @Size(max = 10000)
    private String materialityDetails;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private Set<AviationAerAccreditationReferenceDocumentType> accreditationReferenceDocumentTypes = new HashSet<>();

    @Size(max = 10000)
    private String otherReference;
}
