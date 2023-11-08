package uk.gov.pmrv.api.reporting.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public abstract class AerVerificationReportReferenceValidator implements AerVerificationReportContextValidator {

    @Override
    public AerValidationResult validate(AerVerificationReport verificationReport,
                                        PermitOriginatedData permitOriginatedData) {

        List<AerViolation> aerViolations = new ArrayList<>();
        final Set<String> references = getReferences(verificationReport);

        boolean isFormatValid = references.isEmpty() || references.stream().allMatch(reference ->
            reference.matches("^" + getPrefix() + "[1-9][0-9]*$"));

        if (!isFormatValid) {
            aerViolations.add(new AerViolation(AerVerificationReport.class.getSimpleName(),
                getAerViolationMessage()));
        }

        return AerValidationResult.builder()
            .valid(aerViolations.isEmpty())
            .aerViolations(aerViolations)
            .build();
    }

    public abstract Set<String> getReferences(AerVerificationReport verificationReport);

    public abstract String getPrefix();

    public abstract AerViolation.AerViolationMessage getAerViolationMessage();

}
