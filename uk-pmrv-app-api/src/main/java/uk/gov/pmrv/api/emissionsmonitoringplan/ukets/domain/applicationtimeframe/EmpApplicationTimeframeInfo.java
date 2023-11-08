package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.applicationtimeframe;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#submittedOnTime) == (#reasonForLateSubmission != null)}", message = "emp.applicationTimeframe.reasonForLateSubmission")
public class EmpApplicationTimeframeInfo implements EmpUkEtsSection {

    @NotNull
    LocalDate dateOfStart;

    @NotNull
    Boolean submittedOnTime;

    @Size(max = 2000)
    private String reasonForLateSubmission;
}
