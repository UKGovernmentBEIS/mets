package uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayloadVerifiable;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.MonitoringPlanVersion;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class AerRequestPayload extends RequestPayload implements RequestPayloadVerifiable<AerVerificationReport> {

    private Aer aer;

    private Aer verifiedAer;

    private boolean verificationPerformed;

    private AerVerificationReport verificationReport;

    private boolean virTriggered;

    private PermitOriginatedData permitOriginatedData;

    @Builder.Default
    private Map<AerReviewGroup, AerReviewDecision> reviewGroupDecisions = new EnumMap<>(AerReviewGroup.class);

    @Builder.Default
    private List<MonitoringPlanVersion> monitoringPlanVersions = new ArrayList<>();

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> aerSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> aerAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> verificationAttachments = new HashMap<>();

    private AerSkipReviewDecision skipReviewDecision;

    public void clearRegulatorData() {
        this.reviewGroupDecisions.clear();
        this.reviewAttachments.clear();
        this.reviewSectionsCompleted.clear();
    }
    
    @JsonIgnore
    public AerVerificationData getVerificationData() {
        return verificationReport == null ? null : verificationReport.getVerificationData();
    }
}
