package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement;

import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Transfer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;
import uk.gov.pmrv.api.reporting.service.ApproachEmissionsCalculationService;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class MeasurementApproachEmissionsCalculationService implements ApproachEmissionsCalculationService {

    public BigDecimal getEmissionPointTotalReportableEmissions(MeasurementEmissionPointEmission measurementEmissionPointEmission) {

        BigDecimal emissions = measurementEmissionPointEmission.getCalculationCorrect()
            ? measurementEmissionPointEmission.getReportableEmissions()
            : measurementEmissionPointEmission.getProvidedEmissions().getTotalProvidedReportableEmissions();

        return Objects.nonNull(measurementEmissionPointEmission.getTransfer()) ?
            calculateTransferredEmissions(measurementEmissionPointEmission.getTransfer(),
                emissions) : emissions;
    }

    public abstract BigDecimal calculateTransferredEmissions(Transfer transfer, BigDecimal emissions);
}
