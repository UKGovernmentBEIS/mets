package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaim;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaEmissionsReductionClaimVerification;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;

import java.util.ArrayList;
import java.util.List;

@Service
public class AviationAerCorsiaVerificationReportErcVerificationValidator implements AviationAerCorsiaVerificationReportContextValidator {

    @Override
    public AviationAerValidationResult validate(AviationAerCorsiaVerificationReport verificationReport, AviationAerCorsia aer) {
        List<AviationAerViolation> aerViolations = new ArrayList<>();

        AviationAerCorsiaEmissionsReductionClaim emissionsReductionClaim =
                aer.getEmissionsReductionClaim();
        AviationAerCorsiaEmissionsReductionClaimVerification emissionsReductionClaimVerification =
                verificationReport.getVerificationData().getEmissionsReductionClaimVerification();

        if(Boolean.TRUE.equals(emissionsReductionClaim.getExist()) && ObjectUtils.isEmpty(emissionsReductionClaimVerification) ||
                Boolean.FALSE.equals(emissionsReductionClaim.getExist()) && !ObjectUtils.isEmpty(emissionsReductionClaimVerification)) {
            aerViolations.add(new AviationAerViolation(AviationAerCorsiaEmissionsReductionClaimVerification.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.VERIFICATION_REPORT_INVALID_ERC_VERIFICATION_AND_AER_EMISSIONS_REDUCTION_CLAIM_EXISTS_COMBINATION));
        }

        return AviationAerValidationResult.builder()
                .valid(aerViolations.isEmpty())
                .aerViolations(aerViolations)
                .build();
    }
}
