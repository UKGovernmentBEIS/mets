package uk.gov.pmrv.api.reporting.domain.verification;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{(#accreditationReferenceDocumentTypes?.contains('OTHER')) == (#otherReference != null)}", message = "aerVerificationData.materialityLevel.otherReference")
public class MaterialityLevel {

    @NotBlank
    private String materialityDetails;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<AccreditationReferenceDocumentType> accreditationReferenceDocumentTypes = new ArrayList<>();

    private String otherReference;
}
