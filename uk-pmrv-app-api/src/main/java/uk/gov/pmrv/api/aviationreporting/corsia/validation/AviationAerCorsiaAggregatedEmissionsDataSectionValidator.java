package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerAggregatedEmissionsDataSectionCommonValidator;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionsData;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaAggregatedEmissionsDataSectionValidator implements AviationAerCorsiaContextValidator {

    private final AviationAerAggregatedEmissionsDataSectionCommonValidator commonValidator;

    @Override
    public AviationAerValidationResult validate(final AviationAerCorsiaContainer aerContainer) {

        final Set<AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails =
                aerContainer.getAer().getAggregatedEmissionsData().getAggregatedEmissionDataDetails();

        final AviationAerValidationResult commonResult = commonValidator.validate(aggregatedEmissionDataDetails, aerContainer.getReportingYear());
        if (!commonResult.isValid()) {
            return commonResult;
        }

        final List<AviationAerViolation> aerViolations = new ArrayList<>(commonResult.getAerViolations());

        final List<AviationAerCorsiaAggregatedEmissionDataDetails> equalStateDetails = aggregatedEmissionDataDetails
            .stream()
            .filter(d -> d.getAirportFrom().getState().equals(d.getAirportTo().getState()))
            .toList();
        
        if (!equalStateDetails.isEmpty()) {
            aerViolations.add(new AviationAerViolation(AviationAerCorsiaAggregatedEmissionsData.class.getSimpleName(),
                    AviationAerViolation.AviationAerViolationMessage.INVALID_DEPARTURE_ARRIVAL_STATES, equalStateDetails.toArray()));
        }

        return AviationAerValidationResult.builder()
                .valid(aerViolations.isEmpty())
                .aerViolations(aerViolations)
                .build();
    }
}
