package uk.gov.pmrv.api.migration.permit.monitoringapproaches;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;

import java.util.EnumMap;
import java.util.Map;

@Log4j2
@UtilityClass
public final class MonitoringApproachesListMapper {

    public static MonitoringApproaches constructMonitoringApproaches(EtsMonitoringApproachesList etsMonitoringApproachesList) {
        Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            new EnumMap<>(MonitoringApproachType.class);
        if (etsMonitoringApproachesList.isCalculation()) {
            monitoringApproaches.put(MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder().type(MonitoringApproachType.CALCULATION_CO2).build());
        }
        if (etsMonitoringApproachesList.isFallback()) {
            monitoringApproaches.put(MonitoringApproachType.FALLBACK,
                FallbackMonitoringApproach.builder().type(MonitoringApproachType.FALLBACK).build());
        }
        if (etsMonitoringApproachesList.isMeasurement()) {
            monitoringApproaches.put(MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2MonitoringApproach.builder().type(MonitoringApproachType.MEASUREMENT_CO2).build());
        }
        if (etsMonitoringApproachesList.isPfc()) {
            monitoringApproaches.put(MonitoringApproachType.CALCULATION_PFC, CalculationOfPFCMonitoringApproach.builder().type(MonitoringApproachType.CALCULATION_PFC).build());
        }
        if (etsMonitoringApproachesList.isN2o()) {
            monitoringApproaches.put(MonitoringApproachType.MEASUREMENT_N2O, MeasurementOfN2OMonitoringApproach.builder().type(MonitoringApproachType.MEASUREMENT_N2O).build());
        }
        return MonitoringApproaches.builder()
            .monitoringApproaches(monitoringApproaches)
            .build();
    }
}
