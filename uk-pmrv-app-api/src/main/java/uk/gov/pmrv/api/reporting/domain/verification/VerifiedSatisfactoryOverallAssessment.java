package uk.gov.pmrv.api.reporting.domain.verification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class VerifiedSatisfactoryOverallAssessment extends OverallAssessment {
}
