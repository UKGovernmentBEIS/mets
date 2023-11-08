package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AviationAerCorsiaVerificationReportUncorrectedNonConformitiesValidator extends AviationAerCorsiaVerificationReportReferenceValidator {

    @Override
    public Set<String> getReferences(AviationAerCorsiaVerificationReport verificationReport) {
        return verificationReport.getVerificationData().getUncorrectedNonConformities().getUncorrectedNonConformities()
            .stream()
            .map(UncorrectedItem::getReference)
            .collect(Collectors.toSet());
    }

    @Override
    public String getPrefix() {
        return "B";
    }

    @Override
    public AviationAerViolation.AviationAerViolationMessage getAerViolationMessage() {
        return AviationAerViolation.AviationAerViolationMessage.VERIFICATION_REPORT_INVALID_UNCORRECTED_NON_CONFORMITIES_REFERENCE;
    }
}
