package uk.gov.pmrv.api.reporting.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import jakarta.validation.constraints.NotNull;

@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#accuracy) == (#accuracyReason != null)}", message = "aerVerificationData.complianceMonitoringReporting.accuracyReason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#completeness) == (#completenessReason != null)}", message = "aerVerificationData.complianceMonitoringReporting.completenessReason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#consistency) == (#consistencyReason != null)}", message = "aerVerificationData.complianceMonitoringReporting.consistencyReason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#comparability) == (#comparabilityReason != null)}", message = "aerVerificationData.complianceMonitoringReporting.comparabilityReason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#transparency) == (#transparencyReason != null)}", message = "aerVerificationData.complianceMonitoringReporting.transparencyReason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#integrity) == (#integrityReason != null)}", message = "aerVerificationData.complianceMonitoringReporting.integrityReason")
public class ComplianceMonitoringReporting {

    @NotNull
    private Boolean accuracy;
    private String accuracyReason;

    @NotNull
    private Boolean completeness;
    private String completenessReason;

    @NotNull
    private Boolean consistency;
    private String consistencyReason;

    @NotNull
    private Boolean comparability;
    private String comparabilityReason;

    @NotNull
    private Boolean transparency;
    private String transparencyReason;

    @NotNull
    private Boolean integrity;
    private String integrityReason;
}
