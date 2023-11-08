package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AerFallbackEmissionsInitServiceTest {

    private final AerFallbackEmissionsInitService service = new AerFallbackEmissionsInitService();

    @Test
    void initialize() {
        String sourceStream1 = "sourcestream1";
        String sourceStream2 = "sourcestream2";
        FallbackMonitoringApproach fallbackMonitoringApproach = FallbackMonitoringApproach.builder()
            .type(MonitoringApproachType.FALLBACK)
            .sourceStreamCategoryAppliedTiers(
                List.of(
                    FallbackSourceStreamCategoryAppliedTier.builder()
                        .sourceStreamCategory(FallbackSourceStreamCategory.builder()
                            .sourceStream(sourceStream1)
                            .build())
                        .build(),
                    FallbackSourceStreamCategoryAppliedTier.builder().sourceStreamCategory(FallbackSourceStreamCategory.builder()
                            .sourceStream(sourceStream2)
                            .build())
                        .build()
                )
            )
            .build();

        Permit permit = Permit.builder()
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(Map.of(MonitoringApproachType.FALLBACK, fallbackMonitoringApproach))
                .build())
            .build();

        AerMonitoringApproachEmissions aerMonitoringApproachEmissions = service.initialize(permit);
        assertNotNull(aerMonitoringApproachEmissions);
        assertEquals(MonitoringApproachType.FALLBACK, aerMonitoringApproachEmissions.getType());
        assertThat(aerMonitoringApproachEmissions).isInstanceOf(FallbackEmissions.class);

        FallbackEmissions fallbackEmissions = (FallbackEmissions) aerMonitoringApproachEmissions;

        assertEquals(fallbackEmissions.getSourceStreams(),
            Set.of(sourceStream1, sourceStream2));

    }

    @Test
    void getMonitoringApproachType() {
        assertEquals(MonitoringApproachType.FALLBACK, service.getMonitoringApproachType());
    }
}