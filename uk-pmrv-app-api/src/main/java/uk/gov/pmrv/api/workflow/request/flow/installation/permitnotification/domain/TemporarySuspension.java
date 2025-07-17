package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
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
@SpELExpression(expression = "{#startDateOfNonCompliance != null && #endDateOfNonCompliance != null}",
        message = "permitNotification.dateOfNonCompliance.exist")
public class TemporarySuspension extends PermitNotification {

    @JsonUnwrapped
    @Valid
    private DateOfNonCompliance dateOfNonCompliance;
}
