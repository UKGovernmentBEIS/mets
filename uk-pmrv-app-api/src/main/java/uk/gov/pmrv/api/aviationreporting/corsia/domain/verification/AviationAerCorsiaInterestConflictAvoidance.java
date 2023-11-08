package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#sixVerificationsConducted) == (#breakTaken != null)}", message = "aviationAerVerificationData.corsia.verifierDetails.interestConflictAvoidance.breakTaken")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#breakTaken) == (#reason != null)}", message = "aviationAerVerificationData.corsia.verifierDetails.interestConflictAvoidance.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#sixVerificationsConducted) == (#impartialityAssessmentResult != null)}", message = "aviationAerVerificationData.corsia.verifierDetails.interestConflictAvoidance.impartialityAssessmentResult")
public class AviationAerCorsiaInterestConflictAvoidance {

    @NotNull
    private Boolean sixVerificationsConducted;

    private Boolean breakTaken;

    @Size(max = 10000)
    private String reason;

    @Size(max = 10000)
    private String impartialityAssessmentResult;
}
