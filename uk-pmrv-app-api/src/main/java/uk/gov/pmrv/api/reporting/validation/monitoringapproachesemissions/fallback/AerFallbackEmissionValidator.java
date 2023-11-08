package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.fallback;

import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;

import java.util.List;

public interface AerFallbackEmissionValidator {
    List<AerViolation> validate(FallbackEmissions fallbackEmission, AerContainer aerContainer);
}
