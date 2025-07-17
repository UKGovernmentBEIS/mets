package uk.gov.pmrv.api.reporting.domain.verification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#dataGapRequired) == (#dataGapRequiredDetails != null)}", message = "aerVerificationData.methodologiesToCloseDataGaps.dataGapRequiredDetails")
public class MethodologiesToCloseDataGaps {

    @NotNull
    private Boolean dataGapRequired;

    @Valid
    private DataGapRequiredDetails dataGapRequiredDetails;

}
