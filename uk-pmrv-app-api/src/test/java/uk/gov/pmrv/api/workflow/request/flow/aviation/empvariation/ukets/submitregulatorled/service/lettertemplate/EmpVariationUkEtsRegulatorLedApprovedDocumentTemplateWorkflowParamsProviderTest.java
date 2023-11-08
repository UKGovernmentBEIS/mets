package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service.lettertemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReasonType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;

class EmpVariationUkEtsRegulatorLedApprovedDocumentTemplateWorkflowParamsProviderTest {

	@Test
	void getContextActionType() {
		assertThat(new EmpVariationUkEtsRegulatorLedApprovedDocumentTemplateWorkflowParamsProvider()
				.getContextActionType())
				.isEqualTo(DocumentTemplateGenerationContextActionType.EMP_VARIATION_REGULATOR_LED_APPROVED);
	}
	
	@Test
	void constructParams() {
    	EmpVariationUkEtsRequestPayload payload = EmpVariationUkEtsRequestPayload.builder()
    			.empConsolidationNumber(1)
                .reviewGroupDecisionsRegulatorLed(Map.of(
                		EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
	        						.variationScheduleItems(List.of("sch_abb_1", "sch_abb_2")).build(),
						EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpAcceptedVariationDecisionDetails.builder()
    						.variationScheduleItems(List.of("sch_add_inf_1")).build()
                		))
                .reasonRegulatorLed(EmpVariationUkEtsRegulatorLedReason.builder()
                		.type(EmpVariationUkEtsRegulatorLedReasonType.FOLLOWING_IMPROVING_REPORT)
                		.build())
                .build();
    	String requestId = "1";
    	
        Map<String, Object> result = new EmpVariationUkEtsRegulatorLedApprovedDocumentTemplateWorkflowParamsProvider().constructParams(payload, requestId);

        final Map<String, Object> paramsExpected = new HashMap<>();
        paramsExpected.put("consolidationNumber", payload.getEmpConsolidationNumber());
        paramsExpected.put("variationScheduleItems", List.of("sch_abb_1", "sch_abb_2", 
				"sch_add_inf_1"));
        paramsExpected.put("reason", payload.getReasonRegulatorLed());
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(paramsExpected);
    }
}
