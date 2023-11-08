package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AviationAerUkEtsVerificationReportUncorrectedMisstatementsValidator extends AviationAerUkEtsVerificationReportReferenceValidator {

    @Override
    public Set<String> getReferences(AviationAerUkEtsVerificationReport verificationReport) {
        return verificationReport.getVerificationData().getUncorrectedMisstatements().getUncorrectedMisstatements()
            .stream()
            .map(UncorrectedItem::getReference)
            .collect(Collectors.toSet());
    }

    @Override
    public String getPrefix() {
        return "A";
    }

    @Override
    public AviationAerViolation.AviationAerViolationMessage getAerViolationMessage() {
        return AviationAerViolation.AviationAerViolationMessage.VERIFICATION_REPORT_INVALID_UNCORRECTED_MISSTATEMENT_REFERENCE;
    }
}
