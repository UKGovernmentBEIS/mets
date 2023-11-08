package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AviationAerUkEtsVerificationReportPriorYearIssuesValidator extends AviationAerUkEtsVerificationReportReferenceValidator {

    @Override
    public Set<String> getReferences(AviationAerUkEtsVerificationReport verificationReport) {
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
    public AviationAerViolation.AviationAerViolationMessage getAerViolationMessage() {
        return AviationAerViolation.AviationAerViolationMessage.VERIFICATION_REPORT_INVALID_PRIOR_YEAR_ISSUE_REFERENCE;
    }
}
