package uk.gov.pmrv.api.reporting.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AerVerificationReportUncorrectedNonCompliancesValidator extends AerVerificationReportReferenceValidator {
    @Override
    public Set<String> getReferences(AerVerificationReport verificationReport) {
        return verificationReport.getVerificationData().getUncorrectedNonCompliances().getUncorrectedNonCompliances()
                .stream()
                .map(UncorrectedItem::getReference)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPrefix() {
        return "C";
    }

    @Override
    public AerViolation.AerViolationMessage getAerViolationMessage() {
        return AerViolation.AerViolationMessage.VERIFICATION_INVALID_UNCORRECTED_NON_COMPLIANCES_REFERENCE;
    }
}
