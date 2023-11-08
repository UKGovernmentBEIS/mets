package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerVerificationData;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AviationAerRequestPayload extends RequestPayload {

    private Boolean reportingRequired;

    private boolean virTriggered;

    private AviationAerReportingObligationDetails reportingObligationDetails;

    private boolean verificationPerformed;

    @Builder.Default
    private List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = new ArrayList<>();

    @Builder.Default
    private Map<String, List<Boolean>> aerSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> aerAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    @JsonIgnore
    public abstract AviationAerVerificationData getVerificationData();
}
