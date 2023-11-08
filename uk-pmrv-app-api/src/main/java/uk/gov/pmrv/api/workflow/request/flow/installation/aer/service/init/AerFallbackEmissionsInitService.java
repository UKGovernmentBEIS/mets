package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AerFallbackEmissionsInitService implements AerMonitoringApproachTypeEmissionsInitService {

    @Override
    public AerMonitoringApproachEmissions initialize(Permit permit) {
        final FallbackMonitoringApproach fallbackMonitoringApproach =
            (FallbackMonitoringApproach) permit.getMonitoringApproaches().getMonitoringApproaches().get(getMonitoringApproachType());

        final List<FallbackSourceStreamCategoryAppliedTier> fallbackSourceStreamCategoryAppliedTiers =
            fallbackMonitoringApproach.getSourceStreamCategoryAppliedTiers();

        Set<String> sourceStreams = fallbackSourceStreamCategoryAppliedTiers.stream()
            .map(fallbackSourceStreamCategoryAppliedTier ->
                fallbackSourceStreamCategoryAppliedTier.getSourceStreamCategory().getSourceStream())
            .collect(Collectors.toSet());

        return FallbackEmissions.builder()
            .type(MonitoringApproachType.FALLBACK)
            .sourceStreams(sourceStreams)
            .build();
    }

    @Override
    public MonitoringApproachType getMonitoringApproachType() {
        return MonitoringApproachType.FALLBACK;
    }
}
