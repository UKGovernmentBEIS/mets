package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurementco2;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class EtsMeasEmissionPointCategory {

    private final String etsAccountId;
    
    // source stream
    private final Set<String> sourceStreams;
    private final Set<String> emissionSources;
    private final String emissionPoint;
    private final String estimatedEmission;
    private final String emissionPointCategory;

    //Measured emissions
    private final Set<String> measurementDevices;
    private final String measurementFrequency;
    private final String tierApplied;
    private final String highestTierAppliedJustification;

    //Applied standard
    private final String measurementParameter;
    private final String measurementAppliedStandard;
    private final String measurementDeviationsFromAppliedStandard;
    private final String laboratoryName;
    private final boolean measurementLabIsoAccredited;
    private final String measurementQualityAssuranceMeasures;

}
