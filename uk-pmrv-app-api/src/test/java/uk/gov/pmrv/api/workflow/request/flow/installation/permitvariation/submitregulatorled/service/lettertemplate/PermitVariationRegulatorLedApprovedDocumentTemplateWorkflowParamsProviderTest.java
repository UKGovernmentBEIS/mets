package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.lettertemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReasonTemplate;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountServiceFacade;

@ExtendWith(MockitoExtension.class)
class PermitVariationRegulatorLedApprovedDocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
    private PermitVariationRegulatorLedApprovedDocumentTemplateWorkflowParamsProvider cut;
	
	@Mock
	private PaymentDetermineAmountServiceFacade paymentDetermineAmountServiceFacade;

    @Test
    void getContextActionType() {
        assertThat(cut.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_REGULATOR_LED_APPROVED);
    }

    @Test
    void constructParams() {
    	LocalDate activationDate = LocalDate.now();
    	PermitVariationRequestPayload payload = PermitVariationRequestPayload.builder()
    			.permitConsolidationNumber(1)
    			.permitVariationDetails(PermitVariationDetails.builder()
    					.reason("reason")
    					.build())
                .determinationRegulatorLed(PermitVariationRegulatorLedGrantDetermination.builder()
                		.reasonTemplate(PermitVariationReasonTemplate.OTHER)
                		.reasonTemplateOtherSummary("other template")
                        .activationDate(activationDate)
                        .build())
                .permitVariationDetailsReviewDecisionRegulatorLed(PermitAcceptedVariationDecisionDetails.builder()
               				.variationScheduleItems(List.of("sch_var_details_1", "sch_var_details_2"))
               				.notes("notes")
	                		.build())
                .reviewGroupDecisionsRegulatorLed(Map.of(
                		PermitReviewGroup.ADDITIONAL_INFORMATION, PermitAcceptedVariationDecisionDetails.builder()
	        						.variationScheduleItems(List.of("sch_add_inf_1", "sch_add_inf_2")).build(),
                		PermitReviewGroup.INSTALLATION_DETAILS, PermitAcceptedVariationDecisionDetails.builder()
                						.variationScheduleItems(List.of("sch_inst_details_1")).build()
                		))
                .build();
    	String requestId = "1";
    	
    	when(paymentDetermineAmountServiceFacade.resolveAmount(requestId)).thenReturn(BigDecimal.TEN);
    	
        Map<String, Object> result = cut.constructParams(payload, requestId);

        final Map<String, Object> paramsExpected = new HashMap<>();
        paramsExpected.put("permitConsolidationNumber", payload.getPermitConsolidationNumber());
        paramsExpected.put("activationDate", activationDate);
        paramsExpected.put("variationScheduleItems", List.of("sch_var_details_1", "sch_var_details_2", 
				"sch_inst_details_1", 
				"sch_add_inf_1", "sch_add_inf_2"));
        paramsExpected.put("detailsReason", "reason");
        paramsExpected.put("reasonTemplate", PermitVariationReasonTemplate.OTHER);
        paramsExpected.put("reasonTemplateOther", "other template");
        paramsExpected.put("feeRequired", true); 
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(paramsExpected);
        verify(paymentDetermineAmountServiceFacade, times(1)).resolveAmount(requestId);
    }
    
    @Test
    void constructParams_no_variation_details_review_Decision() {
    	LocalDate activationDate = LocalDate.now();
    	PermitVariationRequestPayload payload = PermitVariationRequestPayload.builder()
    			.permitConsolidationNumber(1)
    			.permitVariationDetails(PermitVariationDetails.builder()
    					.reason("reason")
    					.build())
                .determinationRegulatorLed(PermitVariationRegulatorLedGrantDetermination.builder()
                		.reasonTemplate(PermitVariationReasonTemplate.OTHER)
                		.reasonTemplateOtherSummary("other template")
                        .activationDate(activationDate)
                        .build())
                .permitVariationDetailsReviewDecisionRegulatorLed(null)
                .reviewGroupDecisionsRegulatorLed(Map.of(
                		PermitReviewGroup.ADDITIONAL_INFORMATION, PermitAcceptedVariationDecisionDetails.builder()
	        						.variationScheduleItems(List.of("sch_add_inf_1", "sch_add_inf_2")).build(),
                		PermitReviewGroup.INSTALLATION_DETAILS, PermitAcceptedVariationDecisionDetails.builder()
                						.variationScheduleItems(List.of("sch_inst_details_1")).build()
                		))
                .build();
    	String requestId = "1";
    	
    	when(paymentDetermineAmountServiceFacade.resolveAmount(requestId)).thenReturn(BigDecimal.ZERO);

        Map<String, Object> result = cut.constructParams(payload, requestId);

        final Map<String, Object> paramsExpected = new HashMap<>();
        paramsExpected.put("permitConsolidationNumber", payload.getPermitConsolidationNumber());
        paramsExpected.put("activationDate", activationDate);
        paramsExpected.put("variationScheduleItems", List.of( 
				"sch_inst_details_1", 
				"sch_add_inf_1", "sch_add_inf_2"));
        paramsExpected.put("detailsReason", "reason");
        paramsExpected.put("reasonTemplate", PermitVariationReasonTemplate.OTHER);
        paramsExpected.put("reasonTemplateOther", "other template");
        paramsExpected.put("feeRequired", false); 
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(paramsExpected);
        verify(paymentDetermineAmountServiceFacade, times(1)).resolveAmount(requestId);
    }
}
