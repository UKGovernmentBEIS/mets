package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerEmissionsReductionClaimVerification;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;

import java.util.ArrayList;
import java.util.List;

@Service
public class AviationAerUkEtsVerificationReportErcVerificationValidator implements AviationAerUkEtsVerificationReportContextValidator{

    @Override
    public AviationAerValidationResult validate(AviationAerUkEtsVerificationReport verificationReport) {
        List<AviationAerViolation> aerViolations = new ArrayList<>();

        Boolean safExists = verificationReport.getSafExists();
        AviationAerEmissionsReductionClaimVerification ercVerification =
            verificationReport.getVerificationData().getEmissionsReductionClaimVerification();

        if((Boolean.TRUE.equals(safExists) && ercVerification == null) || (Boolean.FALSE.equals(safExists) && ercVerification != null)) {
            aerViolations.add(new AviationAerViolation(AviationAerEmissionsReductionClaimVerification.class.getSimpleName(),
                AviationAerViolation.AviationAerViolationMessage.VERIFICATION_REPORT_INVALID_ERC_VERIFICATION_AND_SAF_EXISTS_COMBINATION));
        }

        return AviationAerValidationResult.builder()
            .valid(aerViolations.isEmpty())
            .aerViolations(aerViolations)
            .build();
    }
}
