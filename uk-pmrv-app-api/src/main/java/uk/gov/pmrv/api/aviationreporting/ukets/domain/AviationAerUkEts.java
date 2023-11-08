package uk.gov.pmrv.api.aviationreporting.ukets.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerMonitoringPlanChanges;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGaps;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerEmissionsMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerTotalEmissionsConfidentiality;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerUkEts {

    @Valid
    @NotNull
    private EmpAdditionalDocuments additionalDocuments;

    @Valid
    @NotNull
    private AviationOperatorDetails operatorDetails;

    @Valid
    @NotNull
    private AviationAerMonitoringPlanChanges aerMonitoringPlanChanges;

    @Valid
    @NotNull
    private AviationAerEmissionsMonitoringApproach monitoringApproach;

    @Valid
    @NotNull
    private AviationAerUkEtsAggregatedEmissionsData aggregatedEmissionsData;

    @Valid
    private AviationAerDataGaps dataGaps;

    @Valid
    @NotNull
    private AviationAerSaf saf;

    @Valid
    @NotNull
    private AviationAerTotalEmissionsConfidentiality aviationAerTotalEmissionsConfidentiality;

    @Valid
    @NotNull
    private AviationAerAircraftData aviationAerAircraftData;

    @JsonIgnore
    public Set<UUID> getAerSectionAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();

        if (additionalDocuments != null && !ObjectUtils.isEmpty(additionalDocuments.getDocuments())) {
            attachments.addAll(additionalDocuments.getDocuments());
        }
        if (operatorDetails != null && !ObjectUtils.isEmpty(operatorDetails.getAttachmentIds())) {
            attachments.addAll(operatorDetails.getAttachmentIds());
        }
        if (saf != null && !ObjectUtils.isEmpty(saf.getAttachmentIds())) {
            attachments.addAll(saf.getAttachmentIds());
        }
        return Collections.unmodifiableSet(attachments);
    }
}
