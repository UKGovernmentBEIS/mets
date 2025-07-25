package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#inRespectOfMonitoringMethodology) == (#details != null)}", message = "permitNotification.details.inRespectOfMonitoringMethodology")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#inRespectOfMonitoringMethodology) == (#proof != null)}", message = "permitNotification.proof.inRespectOfMonitoringMethodology")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#inRespectOfMonitoringMethodology) == (#measures != null)}", message = "permitNotification.measures.inRespectOfMonitoringMethodology")
@SpELExpression(expression = "{#startDateOfNonCompliance != null && #endDateOfNonCompliance != null}", message = "permitNotification.dateOfNonCompliance.exist")
public class TemporaryFactor extends PermitNotification {

    @JsonUnwrapped
    @Valid
    private DateOfNonCompliance dateOfNonCompliance;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean inRespectOfMonitoringMethodology;

    @Size(max=10000)
    private String details;

    @Size(max=10000)
    private String proof;

    @Size(max=10000)
    private String measures;
}
