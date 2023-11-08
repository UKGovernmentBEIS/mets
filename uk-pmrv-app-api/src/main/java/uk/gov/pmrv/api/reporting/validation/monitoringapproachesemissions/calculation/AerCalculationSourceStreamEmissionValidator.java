package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.util.List;

@Service
public interface AerCalculationSourceStreamEmissionValidator {

    List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, AerContainer aerContainer);
}
