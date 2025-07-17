package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "T(java.lang.Boolean).TRUE.equals(#followUpResponseRequired) == (#followUpRequest != null)",
        message = "permitNotification.reviewDecision.followUpRequest")
@SpELExpression(expression = "T(java.lang.Boolean).TRUE.equals(#followUpResponseRequired) == (#followUpResponseExpirationDate != null)",
        message = "permitNotification.reviewDecision.followUpResponseExpirationDate")
public class FollowUp {

    @NotNull
    private Boolean followUpResponseRequired;

    @Size(max = 10000)
    private String followUpRequest;

    @Future
    private LocalDate followUpResponseExpirationDate;
}
