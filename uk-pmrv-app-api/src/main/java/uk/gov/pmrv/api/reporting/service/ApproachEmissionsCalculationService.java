package uk.gov.pmrv.api.reporting.service;

import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;

import java.math.BigDecimal;

public interface ApproachEmissionsCalculationService {

    BigDecimal getTotalEmissions(AerMonitoringApproachEmissions approachEmissions);

    MonitoringApproachType getType();
}
