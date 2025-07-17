package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AviationAerCorsiaVerificationReportRecommendedImprovementsValidator extends AviationAerCorsiaVerificationReportReferenceValidator {

    @Override
    public Set<String> getReferences(AviationAerCorsiaVerificationReport verificationReport) {
        return verificationReport.getVerificationData().getRecommendedImprovements().getRecommendedImprovements()
            .stream()
            .map(VerifierComment::getReference)
            .collect(Collectors.toSet());
    }

    @Override
    public String getPrefix() {
        return "D";
    }

    @Override
    public AviationAerViolation.AviationAerViolationMessage getAerViolationMessage() {
        return AviationAerViolation.AviationAerViolationMessage.VERIFICATION_REPORT_INVALID_RECOMMENDED_IMPROVEMENT_REFERENCE;
    }
}
