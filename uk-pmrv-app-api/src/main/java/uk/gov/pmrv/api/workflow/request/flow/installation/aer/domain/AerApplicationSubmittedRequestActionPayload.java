package uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.MonitoringPlanVersion;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AerApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    @Valid
    @NotNull
    private Aer aer;

    @NotNull
    private Year reportingYear;

    @Valid
    @NotNull
    private InstallationOperatorDetails installationOperatorDetails;

    private PermitOriginatedData permitOriginatedData;

    @Builder.Default
    private List<MonitoringPlanVersion> monitoringPlanVersions = new ArrayList<>();

    private boolean verificationPerformed;

    private AerVerificationReport verificationReport;

    @Builder.Default
    private Map<UUID, String> aerAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> verificationAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(aerAttachments, verificationAttachments)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
