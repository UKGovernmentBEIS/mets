package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.pfc;

import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;

import java.util.List;

public interface AerCalculationOfPfcSourceStreamEmissionValidator {

    List<AerViolation> validate(PfcSourceStreamEmission pfcSourceStreamEmission, AerContainer aerContainer);
}
