package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSafDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSafPurchase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class AviationAerUkEtsSafSectionValidator implements AviationAerUkEtsContextValidator {

    @Override
    public AviationAerValidationResult validate(AviationAerUkEtsContainer aerContainer) {

        List<AviationAerViolation> aerViolations = new ArrayList<>();

        final Boolean safExist = aerContainer.getAer().getSaf().getExist();
        final AviationAerSafDetails safDetails = aerContainer.getAer().getSaf().getSafDetails();

        if (Boolean.TRUE.equals(safExist)) {
            final BigDecimal calculatedTotalSafMass = calculateTotalSafMass(safDetails.getPurchases());
            if (safDetails.getTotalSafMass().compareTo(calculatedTotalSafMass) != 0) {
                aerViolations.add(new AviationAerViolation(AviationAerSaf.class.getSimpleName(),
                        AviationAerViolation.AviationAerViolationMessage.INVALID_TOTAL_SAF_MASS));
            }

            final BigDecimal calculatedTotalEmissionsReductionClaim = calculateTotalEmissionsReductionClaim(calculatedTotalSafMass);
            if (safDetails.getTotalEmissionsReductionClaim().compareTo(calculatedTotalEmissionsReductionClaim) != 0) {
                aerViolations.add(new AviationAerViolation(AviationAerSaf.class.getSimpleName(),
                        AviationAerViolation.AviationAerViolationMessage.INVALID_TOTAL_EMISSIONS_REDUCTION_CLAIM));
            }
        }

        return AviationAerValidationResult.builder()
                .valid(aerViolations.isEmpty())
                .aerViolations(aerViolations)
                .build();
    }

    private BigDecimal calculateTotalSafMass(List<AviationAerSafPurchase> purchases) {

        return purchases.stream()
                .map(AviationAerSafPurchase::getSafMass)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(3, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalEmissionsReductionClaim(BigDecimal calculatedTotalSafMass) {

        return calculatedTotalSafMass.multiply(BigDecimal.valueOf(3.15))
                .setScale(3, RoundingMode.HALF_UP);
    }
}
