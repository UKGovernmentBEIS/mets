package uk.gov.pmrv.api.reporting.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringPlanDeviation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.nace.NaceCodes;
import uk.gov.pmrv.api.reporting.domain.pollutantregistercodes.PollutantRegisterActivities;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#monitoringApproachEmissions.keySet.contains('MEASUREMENT_CO2') " +
    "|| #monitoringApproachEmissions.keySet.contains('MEASUREMENT_N2O')) == (#emissionPoints != null)}",
    message = "aer.monitoringApproaches.emissionPoints")
public class Aer {

    @Valid
    @NotNull
    private AdditionalDocuments additionalDocuments;

    @Valid
    @NotNull
    private Abbreviations abbreviations;

    @Valid
    @NotNull
    private ConfidentialityStatement confidentialityStatement;

    @Valid
    @NotNull
    private PollutantRegisterActivities pollutantRegisterActivities;

    @Valid
    @NotNull
    private NaceCodes naceCodes;

    @JsonUnwrapped
    @Valid
    @NotNull
    private SourceStreams sourceStreams;

    @JsonUnwrapped
    @Valid
    @NotNull
    private EmissionSources emissionSources;

    @JsonUnwrapped
    private EmissionPoints emissionPoints;

    @JsonUnwrapped
    @Valid
    @NotNull
    private MonitoringApproachEmissions monitoringApproachEmissions;

    @JsonUnwrapped
    @Valid
    @NotNull
    private AerRegulatedActivities regulatedActivities;

    @Valid
    @NotNull
    private AerMonitoringPlanDeviation aerMonitoringPlanDeviation;

    @Valid
    private ActivityLevelReport activityLevelReport;

    @JsonIgnore
    public Set<UUID> getAerSectionAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();

        if (additionalDocuments != null && !ObjectUtils.isEmpty(additionalDocuments.getDocuments())) {
            attachments.addAll(additionalDocuments.getDocuments());
        }

        if (monitoringApproachEmissions != null) {
            attachments.addAll(monitoringApproachEmissions.getAttachmentIds());
        }

        if (activityLevelReport != null && activityLevelReport.getFile() != null) {
            attachments.add(activityLevelReport.getFile());
        }

        return Collections.unmodifiableSet(attachments);
    }

    @JsonIgnore
    public Set<String> getSourceStreamsIds() {

        return this.getSourceStreams().getSourceStreams().stream()
            .map(SourceStream::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<String> getEmissionSourcesIds() {

        return this.getEmissionSources().getEmissionSources().stream()
            .map(EmissionSource::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<String> getEmissionPointsIds() {

        return this.getEmissionPoints().getEmissionPoints().stream()
            .map(EmissionPoint::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}
