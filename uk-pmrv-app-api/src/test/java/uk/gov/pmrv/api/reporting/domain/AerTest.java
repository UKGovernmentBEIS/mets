package uk.gov.pmrv.api.reporting.domain;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AerTest {

    @Test
    void getAerSectionAttachmentIds() {
        Set<UUID> fallbackFiles = Set.of(UUID.randomUUID());
        Set<UUID> documents = Set.of(UUID.randomUUID());
        Set<UUID> expected = Stream.concat(fallbackFiles.stream(), documents.stream()).collect(Collectors.toSet());
        Aer aer = Aer.builder()
            .additionalDocuments(AdditionalDocuments.builder()
                .documents(documents)
                .build())
            .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                .monitoringApproachEmissions(Map.of(
                    MonitoringApproachType.FALLBACK,
                    FallbackEmissions.builder().files(fallbackFiles).build()
                ))
                .build())
            .build();

        Set<UUID> aerSectionAttachmentIds = aer.getAerSectionAttachmentIds();
        assertEquals(expected, aerSectionAttachmentIds);
    }
}