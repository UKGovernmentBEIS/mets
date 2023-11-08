package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ManuallyProvidedEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;

import java.util.ArrayList;
import java.util.List;

@Service
public class AerManuallyProvidedEmissionsValidator implements AerMeasurementEmissionPointEmissionValidator {

    @Override
    public List<AerViolation> validate(MeasurementEmissionPointEmission measurementEmissionPointEmission,
                                       AerContainer aerContainer) {
        List<AerViolation> violations = new ArrayList<>();

        if (Boolean.FALSE.equals(measurementEmissionPointEmission.getCalculationCorrect())) {
            ManuallyProvidedEmissions manuallyProvidedEmissions =
                measurementEmissionPointEmission.getProvidedEmissions();
            if (Boolean.TRUE.equals(measurementEmissionPointEmission.getBiomassPercentages().getContains())
                && manuallyProvidedEmissions.getTotalProvidedSustainableBiomassEmissions() == null) {
                violations.add(new AerViolation(MeasurementEmissionPointEmission.class.getSimpleName(),
                    AerViolation.AerViolationMessage.TOTAL_SUSTAINABLE_BIOMASS_EMISSION_ARE_MISSING));
            }
        }
        return violations;
    }
}
