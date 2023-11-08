package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2Direction;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethod;
import uk.gov.pmrv.api.reporting.service.ApproachEmissionsCalculationService;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class CalculationApproachEmissionsCalculationService implements ApproachEmissionsCalculationService {

    private static final int NEGATIVE_AMOUNT = -1;

    @Override
    public BigDecimal getTotalEmissions(AerMonitoringApproachEmissions approachEmissions) {
        return ((CalculationOfCO2Emissions) approachEmissions).getSourceStreamEmissions().stream()
            .map(calculationSourceStreamEmission ->
                getSourceStreamTotalReportableEmissions(
                    calculationSourceStreamEmission.getParameterCalculationMethod(),
                    calculationSourceStreamEmission.getTransfer())
            )
            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    @Override
    public MonitoringApproachType getType() {
        return MonitoringApproachType.CALCULATION_CO2;
    }

    private BigDecimal getSourceStreamTotalReportableEmissions(
        CalculationParameterCalculationMethod calculationParameterCalculationMethod,
        TransferCO2 transfer) {

        CalculationEmissionCalculationParamValues paramValues = calculationParameterCalculationMethod
            .getEmissionCalculationParamValues();

        BigDecimal emissions = paramValues.getCalculationCorrect()
            ? paramValues.getTotalReportableEmissions()
            : paramValues.getProvidedEmissions().getTotalProvidedReportableEmissions();

        return Objects.nonNull(transfer) ? calculateTransferredEmissions(transfer, emissions) : emissions;
    }

    private BigDecimal calculateTransferredEmissions(TransferCO2 transfer, BigDecimal emissions) {
        emissions = emissions.abs();
        return transfer.getTransferDirection() != TransferCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION ?
            emissions.multiply(BigDecimal.valueOf(NEGATIVE_AMOUNT)) : emissions;
    }
}
