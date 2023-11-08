package uk.gov.pmrv.api.aviationreporting.corsia.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerMonitoringPlanChanges;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.confidentiality.AviationAerCorsiaConfidentiality;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps.AviationAerCorsiaDataGaps;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaim;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerCorsia {

    @Valid
    @NotNull
    private EmpAdditionalDocuments additionalDocuments;

    @Valid
    @NotNull
    private AviationCorsiaOperatorDetails operatorDetails;

    @Valid
    @NotNull
    private AviationAerMonitoringPlanChanges aerMonitoringPlanChanges;

    @Valid
    @NotNull
    private AviationAerCorsiaAggregatedEmissionsData aggregatedEmissionsData;

    @Valid
    @NotNull
    private AviationAerCorsiaEmissionsReductionClaim emissionsReductionClaim;

    @Valid
    @NotNull
    private AviationAerCorsiaMonitoringApproach monitoringApproach;

    @Valid
    @NotNull
    private AviationAerCorsiaDataGaps dataGaps;

    @Valid
    @NotNull
    private AviationAerCorsiaConfidentiality confidentiality;

    @Valid
    @NotNull
    private AviationAerAircraftData aviationAerAircraftData;

    @JsonIgnore
    public Set<UUID> getAerSectionAttachmentIds() {

        final Set<UUID> attachments = new HashSet<>();

        if (operatorDetails != null && !ObjectUtils.isEmpty(operatorDetails.getAttachmentIds())) {
            attachments.addAll(operatorDetails.getAttachmentIds());
        }
        if (additionalDocuments != null && !ObjectUtils.isEmpty(additionalDocuments.getDocuments())) {
            attachments.addAll(additionalDocuments.getDocuments());
        }
        if (emissionsReductionClaim != null && !ObjectUtils.isEmpty(emissionsReductionClaim.getAttachmentIds())) {
            attachments.addAll(emissionsReductionClaim.getAttachmentIds());
        }
        if (confidentiality != null && !ObjectUtils.isEmpty(confidentiality.getAttachmentIds())) {
            attachments.addAll(confidentiality.getAttachmentIds());
        }
        return Collections.unmodifiableSet(attachments);
    }
}
