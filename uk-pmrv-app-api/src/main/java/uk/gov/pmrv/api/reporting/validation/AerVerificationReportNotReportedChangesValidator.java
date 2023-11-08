package uk.gov.pmrv.api.reporting.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AerVerificationReportNotReportedChangesValidator extends AerVerificationReportReferenceValidator {

    @Override
    public Set<String> getReferences(AerVerificationReport verificationReport) {
        return verificationReport.getVerificationData().getSummaryOfConditions().getNotReportedChanges()
                .stream()
                .map(VerifierComment::getReference)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPrefix() {
        return "B";
    }

    @Override
    public AerViolation.AerViolationMessage getAerViolationMessage() {
        return AerViolation.AerViolationMessage.VERIFICATION_INVALID_NOT_REPORTED_CHANGE_REFERENCE;
    }
}
