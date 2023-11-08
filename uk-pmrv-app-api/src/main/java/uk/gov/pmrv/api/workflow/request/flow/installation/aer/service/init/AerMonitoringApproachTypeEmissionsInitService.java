package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;

public interface AerMonitoringApproachTypeEmissionsInitService {

    AerMonitoringApproachEmissions initialize(Permit permit);

    MonitoringApproachType getMonitoringApproachType();
}
