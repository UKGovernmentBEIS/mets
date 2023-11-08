package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.common.domain.aggregatedemissionsdata.AviationAerAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGap;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGaps;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AviationAerUkEtsDataGapsSectionValidator implements AviationAerUkEtsContextValidator {

    @Override
    public AviationAerValidationResult validate(AviationAerUkEtsContainer aerContainer) {

        List<AviationAerViolation> aerViolations = new ArrayList<>();

        final EmissionsMonitoringApproachType monitoringApproachType = aerContainer.getAer().getMonitoringApproach().getMonitoringApproachType();
        final AviationAerDataGaps dataGaps = aerContainer.getAer().getDataGaps();
        final Set<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails =
                aerContainer.getAer().getAggregatedEmissionsData().getAggregatedEmissionDataDetails();

        if ((EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(monitoringApproachType) && dataGaps == null) ||
                (!EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(monitoringApproachType) && dataGaps != null)) {
            aerViolations.add(new AviationAerViolation(AviationAerDataGaps.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.INVALID_DATA_GAPS));
        }

        if (EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(monitoringApproachType) && dataGaps != null && Boolean.TRUE.equals(dataGaps.getExist())) {
            final BigDecimal calculatedPercentage = calculateAffectedFlightsPercentage(aggregatedEmissionDataDetails, dataGaps);
            if (dataGaps.getAffectedFlightsPercentage().compareTo(calculatedPercentage) != 0) {
                aerViolations.add(new AviationAerViolation(AviationAerDataGaps.class.getSimpleName(),
                        AviationAerViolation.AviationAerViolationMessage.INVALID_AFFECTED_FLIGHTS_PERCENTAGE));
            }
        }

        return AviationAerValidationResult.builder()
                .valid(aerViolations.isEmpty())
                .aerViolations(aerViolations)
                .build();
    }

    private BigDecimal calculateAffectedFlightsPercentage(Set<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails,
                                                          AviationAerDataGaps dataGaps) {

        final Integer totalFlightsNumber = aggregatedEmissionDataDetails.stream()
                .map(AviationAerAggregatedEmissionDataDetails::getFlightsNumber)
                .reduce(0, Integer::sum);
        final Integer totalAffectedFlightsNumber = dataGaps.getDataGaps().stream()
                .map(AviationAerDataGap::getFlightsAffected)
                .reduce(0, Integer::sum);

        return BigDecimal.valueOf(totalAffectedFlightsNumber)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalFlightsNumber), 1, RoundingMode.HALF_UP);
    }

}
