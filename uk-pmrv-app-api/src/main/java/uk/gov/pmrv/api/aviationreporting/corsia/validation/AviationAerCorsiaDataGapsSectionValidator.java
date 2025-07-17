package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.common.domain.aggregatedemissionsdata.AviationAerAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps.AviationAerCorsiaDataGaps;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps.AviationAerCorsiaDataGapsPercentageType;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationAerCorsiaSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaDataGapsSectionValidator implements AviationAerCorsiaContextValidator {

    private final AviationAerCorsiaSubmittedEmissionsCalculationService emissionsCalculationService;

    @Override
    public AviationAerValidationResult validate(AviationAerCorsiaContainer aerContainer) {

        List<AviationAerViolation> aerViolations = new ArrayList<>();

        final AviationAerCorsiaDataGaps dataGaps = aerContainer.getAer().getDataGaps();

		if (dataGaps != null && 
				Boolean.TRUE.equals(dataGaps.getExist() && 
						dataGaps.getDataGapsDetails().getDataGapsPercentageType() == AviationAerCorsiaDataGapsPercentageType.MORE_THAN_FIVE_PER_CENT)) {
            final Set<AviationAerCorsiaAggregatedEmissionDataDetails> offsettingFlightsEmissionsDataDetails =
                    emissionsCalculationService.findOffsettingFlights(aerContainer.getAer().getAggregatedEmissionsData().getAggregatedEmissionDataDetails(), aerContainer.getReportingYear());

            final Integer offsetFlightsNumber = offsettingFlightsEmissionsDataDetails.stream()
                    .map(AviationAerAggregatedEmissionDataDetails::getFlightsNumber)
                    .reduce(0, Integer::sum);

            final BigDecimal calculatedPercentage = calculateAffectedFlightsPercentage(dataGaps, offsetFlightsNumber);
            if (dataGaps.getDataGapsDetails().getAffectedFlightsPercentage().compareTo(calculatedPercentage) != 0) {
                aerViolations.add(new AviationAerViolation(AviationAerCorsiaDataGaps.class.getSimpleName(),
                        AviationAerViolation.AviationAerViolationMessage.INVALID_AFFECTED_FLIGHTS_PERCENTAGE));
            }
        }

        return AviationAerValidationResult.builder()
                .valid(aerViolations.isEmpty())
                .aerViolations(aerViolations)
                .build();
    }

    private BigDecimal calculateAffectedFlightsPercentage(AviationAerCorsiaDataGaps dataGaps, Integer offsetFlightsNumber) {

        final Integer totalAffectedFlightsNumber = dataGaps.getDataGapsDetails().getDataGaps().stream()
                .map(AviationAerDataGap::getFlightsAffected)
                .reduce(0, Integer::sum);

        return offsetFlightsNumber.compareTo(0) == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(totalAffectedFlightsNumber)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(offsetFlightsNumber), 1, RoundingMode.HALF_UP);
    }
}
