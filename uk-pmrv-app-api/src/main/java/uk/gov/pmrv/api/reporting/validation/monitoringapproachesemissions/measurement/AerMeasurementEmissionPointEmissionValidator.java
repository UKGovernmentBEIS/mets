package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement;

import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;

import java.util.List;

public interface AerMeasurementEmissionPointEmissionValidator {
    List<AerViolation> validate(MeasurementEmissionPointEmission measurementEmissionPointEmission, AerContainer aerContainer);
}
