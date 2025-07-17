package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{{'EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT','EXCEEDED_THRESHOLD_STATED_HSE_PERMIT'}.?[#this == #reportingType].empty || (#startDateOfNonCompliance != null && #endDateOfNonCompliance == null)}",
        message = "permitNotification.startDateOfNonCompliance.reportingType")
@SpELExpression(expression = "{#reportingType != 'RENOUNCE_FREE_ALLOCATIONS' || ((#startDateOfNonCompliance == null) && (#endDateOfNonCompliance == null))}",
        message = "permitNotification.startDateOfNonCompliance.reportingType")
public class OtherFactor extends PermitNotification {

    @NotNull
    private ReportingType reportingType;

    @JsonUnwrapped
    @Valid
    private DateOfNonCompliance dateOfNonCompliance;
}
