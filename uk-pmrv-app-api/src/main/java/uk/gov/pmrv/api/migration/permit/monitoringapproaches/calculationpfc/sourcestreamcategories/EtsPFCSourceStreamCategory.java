package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationpfc.sourcestreamcategories;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class EtsPFCSourceStreamCategory {

    private final String etsAccountId;

    // source stream
    private final String sourceStream;
    private final Set<String> emissionSources;
    private final String estimatedEmission;
    private final String sourceStreamCategory;
    private final Set<String> emissionPoints;
    private final String calculationMethod;

    // activity data
    private final boolean massBalanceApproachUsed;
    private final String activityDataTierApplied;
    private final boolean activityDataIsMiddleTier;
    private final String tierJustification;

    // emission factor
    private final String emissionFactorTierApplied;
    private final boolean emissionFactorIsMiddleTier;

}
