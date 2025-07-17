package uk.gov.pmrv.api.aviationreporting.common.domain.verification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class AviationAerVerifiedSatisfactoryDecision extends AviationAerVerificationDecision {
}
