package uk.gov.pmrv.api.reporting.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#dataGapApproved) == (#dataGapApprovedDetails != null)}", message = "aerVerificationData.methodologiesToCloseDataGaps.dataGapApprovedDetails")
public class DataGapRequiredDetails {

    @NotNull
    private Boolean dataGapApproved;

    @Valid
    private DataGapApprovedDetails dataGapApprovedDetails;
}
