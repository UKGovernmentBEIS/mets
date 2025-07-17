package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
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
public class EmpVariationCorsiaRequestPayload extends RequestPayload 
	implements RequestPayloadPayable, RequestPayloadRdeable, RequestPayloadRfiable{
	
	private EmissionsMonitoringPlanCorsiaContainer originalEmpContainer;
	
	private EmissionsMonitoringPlanCorsia emissionsMonitoringPlan;
	
	private EmpVariationCorsiaDetails empVariationDetails;
	
	private String reasonRegulatorLed;

	private Map<EmpCorsiaReviewGroup, EmpAcceptedVariationDecisionDetails> reviewGroupDecisionsRegulatorLed;
	
	private Boolean empVariationDetailsCompleted;
	
	@Builder.Default
	private Map<String, List<Boolean>> empSectionsCompleted = new HashMap<>();

	@Builder.Default
	private Map<UUID, String> empAttachments = new HashMap<>();
	
	@Builder.Default
	private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
	
	private Boolean empVariationDetailsReviewCompleted;
	
	@Builder.Default
    private Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpCorsiaReviewGroup.class);
	
	private EmpVariationReviewDecision empVariationDetailsReviewDecision;

	private DecisionNotification decisionNotification;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Integer empConsolidationNumber;
	
    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();
    
    private EmpVariationDetermination determination;

    @JsonUnwrapped
    private RfiData rfiData;

    @JsonUnwrapped
    private RdeData rdeData;

	private FileInfoDTO officialNotice;

	private FileInfoDTO empDocument;

	private RequestPaymentInfo requestPaymentInfo;
        
}
