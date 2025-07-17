package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

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
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#accuracyCompliant) == (#accuracyNonCompliantReason != null)}", message = "aviationAerVerificationData.complianceMonitoringReportingRules.accuracyCompliant.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#completenessCompliant) == (#completenessNonCompliantReason != null)}", message = "aviationAerVerificationData.complianceMonitoringReportingRules.completenessCompliant.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#consistencyCompliant) == (#consistencyNonCompliantReason != null)}", message = "aviationAerVerificationData.complianceMonitoringReportingRules.consistencyCompliant.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#comparabilityCompliant) == (#comparabilityNonCompliantReason != null)}", message = "aviationAerVerificationData.complianceMonitoringReportingRules.comparabilityCompliant.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#transparencyCompliant) == (#transparencyNonCompliantReason != null)}", message = "aviationAerVerificationData.complianceMonitoringReportingRules.transparencyCompliant.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#integrityCompliant) == (#integrityNonCompliantReason != null)}", message = "aviationAerVerificationData.complianceMonitoringReportingRules.integrityCompliant.reason")
public class AviationAerComplianceMonitoringReportingRules {

    @NotNull
    private Boolean accuracyCompliant;
    private String accuracyNonCompliantReason;

    @NotNull
    private Boolean completenessCompliant;
    private String completenessNonCompliantReason;

    @NotNull
    private Boolean consistencyCompliant;
    private String consistencyNonCompliantReason;

    @NotNull
    private Boolean comparabilityCompliant;
    private String comparabilityNonCompliantReason;

    @NotNull
    private Boolean transparencyCompliant;
    private String transparencyNonCompliantReason;

    @NotNull
    private Boolean integrityCompliant;
    private String integrityNonCompliantReason;
}
