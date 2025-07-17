package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerUkEtsSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;

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
public class AviationAerUkEtsApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    private Boolean reportingRequired;

    private AviationAerReportingObligationDetails reportingObligationDetails;

    private AviationAerUkEts aer;

    private Year reportingYear;

    private ServiceContactDetails serviceContactDetails;

    @Builder.Default
    private List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = new ArrayList<>();

    private boolean verificationPerformed;

    private AviationAerUkEtsSubmittedEmissions submittedEmissions;

    private AviationAerUkEtsVerificationReport verificationReport;

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
