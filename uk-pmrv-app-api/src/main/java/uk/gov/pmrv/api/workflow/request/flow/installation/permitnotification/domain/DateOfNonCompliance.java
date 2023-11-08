package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#startDateOfNonCompliance == null) || (#endDateOfNonCompliance == null) || " +
        "T(java.time.LocalDate).parse(#startDateOfNonCompliance).isBefore(T(java.time.LocalDate).parse(#endDateOfNonCompliance))}",
        message = "permitNotification.endDateOfNonCompliance.startDateOfNonCompliance")
public class DateOfNonCompliance {

    private LocalDate startDateOfNonCompliance;

    private LocalDate endDateOfNonCompliance;
}
