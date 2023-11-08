package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitPayloadDecidableAndDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PermitVariationRequestPayload extends RequestPayload
		implements PermitPayloadDecidableAndDeterminateable<PermitVariationReviewDecision, PermitVariationDeterminateable>,
		RequestPayloadRfiable, RequestPayloadRdeable, RequestPayloadPayable {

	private PermitContainer originalPermitContainer;
	
	private PermitType permitType;

	private Permit permit;
	
	private PermitVariationDetails permitVariationDetails;
	
	private Boolean permitVariationDetailsCompleted;

	@Builder.Default
	private Map<String, List<Boolean>> permitSectionsCompleted = new HashMap<>();

	@Builder.Default
	private Map<UUID, String> permitAttachments = new HashMap<>();
	
	@Builder.Default
	private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
	
	private Boolean permitVariationDetailsReviewCompleted;
	
	@Builder.Default
    private Map<PermitReviewGroup, PermitVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(PermitReviewGroup.class);
	
	private PermitVariationReviewDecision permitVariationDetailsReviewDecision;
	
    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();
	
	private PermitVariationDeterminateable determination;
	
	private DecisionNotification decisionNotification;
	
	/** Regulator LED property */
	private PermitAcceptedVariationDecisionDetails permitVariationDetailsReviewDecisionRegulatorLed;
	
	/** Regulator LED property */
    private Map<PermitReviewGroup, PermitAcceptedVariationDecisionDetails> reviewGroupDecisionsRegulatorLed;
	
	/** Regulator LED property */
	private PermitVariationRegulatorLedGrantDetermination determinationRegulatorLed;	
	
	@JsonUnwrapped
    private RfiData rfiData;
	
	@JsonUnwrapped
    private RdeData rdeData;

	@JsonInclude(Include.NON_EMPTY)
	private Integer permitConsolidationNumber;
	
    private RequestPaymentInfo requestPaymentInfo;
    
    private FileInfoDTO officialNotice;
    
    private FileInfoDTO permitDocument;
    
    @JsonIgnore
    public boolean isRegulatorLed() {
		return permitVariationDetailsReviewDecisionRegulatorLed != null || 
				reviewGroupDecisionsRegulatorLed != null || 
				determinationRegulatorLed != null;
    }
}
