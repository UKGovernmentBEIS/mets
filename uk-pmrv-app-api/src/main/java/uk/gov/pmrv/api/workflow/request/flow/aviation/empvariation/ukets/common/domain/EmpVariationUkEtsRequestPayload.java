package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReason;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpVariationUkEtsRequestPayload extends RequestPayload 
	implements RequestPayloadPayable, RequestPayloadRdeable, RequestPayloadRfiable {
	
	private EmissionsMonitoringPlanUkEtsContainer originalEmpContainer;

	private EmissionsMonitoringPlanUkEts emissionsMonitoringPlan;
	
	private EmpVariationUkEtsDetails empVariationDetails;
	
	private Boolean empVariationDetailsCompleted;
	
	@Builder.Default
	private Map<String, List<Boolean>> empSectionsCompleted = new HashMap<>();

	@Builder.Default
	private Map<UUID, String> empAttachments = new HashMap<>();
	
	@Builder.Default
	private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
	
	private Boolean empVariationDetailsReviewCompleted;
	
	@Builder.Default
    private Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
	
	private EmpVariationReviewDecision empVariationDetailsReviewDecision;
	
    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();
    
	/** Regulator LED property */
    private Map<EmpUkEtsReviewGroup, EmpAcceptedVariationDecisionDetails> reviewGroupDecisionsRegulatorLed;
	
	/** Regulator LED property */
	private EmpVariationUkEtsRegulatorLedReason reasonRegulatorLed;	
    
    private EmpVariationDetermination determination;
    
    private DecisionNotification decisionNotification;
    
    @JsonInclude(Include.NON_EMPTY)
	private Integer empConsolidationNumber;

    private RequestPaymentInfo requestPaymentInfo;
    
    @JsonUnwrapped
    private RfiData rfiData;

    @JsonUnwrapped
    private RdeData rdeData;
    
    private FileInfoDTO officialNotice;
    
    private FileInfoDTO empDocument;
	
}
