package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerCorsiaVerifierDetails {

    @NotNull
    @Valid
    private AviationAerCorsiaVerificationTeamLeader verificationTeamLeader;

    @NotNull
    @Valid
    private AviationAerCorsiaInterestConflictAvoidance interestConflictAvoidance;
}
