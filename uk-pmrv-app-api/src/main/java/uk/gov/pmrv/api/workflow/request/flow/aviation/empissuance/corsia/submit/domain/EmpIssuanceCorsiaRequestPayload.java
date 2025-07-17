package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpIssuanceCorsiaRequestPayload extends RequestPayload implements RequestPayloadPayable,
    RequestPayloadRdeable, RequestPayloadRfiable {

    private EmissionsMonitoringPlanCorsia emissionsMonitoringPlan;

    @Builder.Default
    private Map<String, List<Boolean>> empSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> empAttachments = new HashMap<>();

    private RequestPaymentInfo requestPaymentInfo;

    @JsonUnwrapped
    private RfiData rfiData;

    @JsonUnwrapped
    private RdeData rdeData;

    private EmpIssuanceDetermination determination;

    private DecisionNotification decisionNotification;

    private FileInfoDTO officialNotice;

    private FileInfoDTO empDocument;

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<EmpCorsiaReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpCorsiaReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();
}