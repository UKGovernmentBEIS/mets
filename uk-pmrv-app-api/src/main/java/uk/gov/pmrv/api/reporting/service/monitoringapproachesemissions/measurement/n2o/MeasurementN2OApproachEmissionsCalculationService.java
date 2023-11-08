package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.n2o;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Transfer;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.TransferN2O;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.TransferN2ODirection;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.MeasurementApproachEmissionsCalculationService;

import java.math.BigDecimal;

@Service
public class MeasurementN2OApproachEmissionsCalculationService extends MeasurementApproachEmissionsCalculationService {

    private static final int NEGATIVE_AMOUNT = -1;

    @Override
    public BigDecimal getTotalEmissions(AerMonitoringApproachEmissions approachEmissions) {
        return ((MeasurementOfN2OEmissions) approachEmissions).getEmissionPointEmissions().stream()
            .map(this::getEmissionPointTotalReportableEmissions
            )
            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    @Override
    public MonitoringApproachType getType() {
        return MonitoringApproachType.MEASUREMENT_N2O;
    }

    @Override
    public BigDecimal calculateTransferredEmissions(Transfer transfer, BigDecimal emissions) {
        emissions = emissions.abs();
        return ((TransferN2O) transfer).getTransferDirection() != TransferN2ODirection.RECEIVED_FROM_ANOTHER_INSTALLATION ?
            emissions.multiply(BigDecimal.valueOf(NEGATIVE_AMOUNT)) : emissions;
    }
}
