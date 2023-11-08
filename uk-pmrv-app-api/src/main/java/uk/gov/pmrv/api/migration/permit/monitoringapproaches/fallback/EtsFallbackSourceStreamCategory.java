package uk.gov.pmrv.api.migration.permit.monitoringapproaches.fallback;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class EtsFallbackSourceStreamCategory {

    private final String etsAccountId;

    private final String sourceStream;
    private final Set<String> emissionSources;
    private final String estimatedEmission;
    private final String sourceStreamCategory;
    private final Set<String> measurementDevices;
    private final String meteringUncertainty;

}
