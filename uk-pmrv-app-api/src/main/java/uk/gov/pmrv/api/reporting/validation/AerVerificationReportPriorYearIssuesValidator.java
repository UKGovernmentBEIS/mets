package uk.gov.pmrv.api.reporting.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AerVerificationReportPriorYearIssuesValidator extends AerVerificationReportReferenceValidator {
    @Override
    public Set<String> getReferences(AerVerificationReport verificationReport) {
        return verificationReport.getVerificationData().getUncorrectedNonConformities().getPriorYearIssues()
                .stream()
                .map(VerifierComment::getReference)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPrefix() {
        return "E";
    }

    @Override
    public AerViolation.AerViolationMessage getAerViolationMessage() {
        return AerViolation.AerViolationMessage.VERIFICATION_INVALID_PRIOR_YEAR_ISSUE_REFERENCE;
    }
}
