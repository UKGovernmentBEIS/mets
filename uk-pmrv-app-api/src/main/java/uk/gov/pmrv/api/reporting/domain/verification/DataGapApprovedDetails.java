package uk.gov.pmrv.api.reporting.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#conservativeMethodUsed) == (#methodDetails != null)}", message = "aerVerificationData.methodologiesToCloseDataGaps.methodDetails")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#materialMisstatement) == (#materialMisstatementDetails != null)}", message = "aerVerificationData.methodologiesToCloseDataGaps.materialMisstatementDetails")
public class DataGapApprovedDetails {

    @NotNull
    private Boolean conservativeMethodUsed;

    @Size(max = 10000)
    private String methodDetails;

    @NotNull
    private Boolean materialMisstatement;

    @Size(max = 10000)
    private String materialMisstatementDetails;
}
