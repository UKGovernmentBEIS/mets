package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.time.Year;
import java.util.List;

public interface AerCalculationSourceStreamParamCalcMethodEmissionsValidator {

    List<AerViolation> validate(CalculationSourceStreamEmission sourceStreamEmission, SourceStreamType sourceStreamType,
                                Year reportingYear);

    CalculationParameterCalculationMethodType getParameterCalculationMethodType();


}
