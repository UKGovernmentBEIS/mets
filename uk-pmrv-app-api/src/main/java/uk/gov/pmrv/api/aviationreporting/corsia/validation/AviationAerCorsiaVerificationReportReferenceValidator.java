package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public abstract class AviationAerCorsiaVerificationReportReferenceValidator implements AviationAerCorsiaVerificationReportContextValidator {

    @Override
    public AviationAerValidationResult validate(AviationAerCorsiaVerificationReport verificationReport, AviationAerCorsia aer) {
        List<AviationAerViolation> aerViolations = new ArrayList<>();
        final Set<String> references = getReferences(verificationReport);

        boolean isFormatValid = references.isEmpty() || references.stream().allMatch(reference ->
            reference.matches("^" + getPrefix() + "[1-9][0-9]*$"));

        if (!isFormatValid) {
            aerViolations.add(new AviationAerViolation(AviationAerCorsiaVerificationReport.class.getSimpleName(),
                getAerViolationMessage()));
        }

        return AviationAerValidationResult.builder()
            .valid(aerViolations.isEmpty())
            .aerViolations(aerViolations)
            .build();
    }

    public abstract Set<String> getReferences(AviationAerCorsiaVerificationReport verificationReport);

    public abstract String getPrefix();

    public abstract AviationAerViolation.AviationAerViolationMessage getAerViolationMessage();
}
