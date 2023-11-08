package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service.lettertemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaRegulatorLedApprovedDocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
	private EmpVariationCorsiaRegulatorLedApprovedDocumentTemplateWorkflowParamsProvider provider;
	
	@Test
	void getContextActionType() {
		assertThat(new EmpVariationCorsiaRegulatorLedApprovedDocumentTemplateWorkflowParamsProvider()
				.getContextActionType())
				.isEqualTo(DocumentTemplateGenerationContextActionType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED);
	}
	
	@Test
	void constructParams() {
    	EmpVariationCorsiaRequestPayload payload = EmpVariationCorsiaRequestPayload.builder()
    			.empConsolidationNumber(1)
                .reviewGroupDecisionsRegulatorLed(Map.of(
                		EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpAcceptedVariationDecisionDetails.builder()
	        						.variationScheduleItems(List.of("sch_abb_1", "sch_abb_2")).build(),
					EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpAcceptedVariationDecisionDetails.builder()
    						.variationScheduleItems(List.of("sch_add_inf_1")).build()
                		))
                .reasonRegulatorLed("reasonRegulatorLed")
                .build();
    	String requestId = "1";
    	
        Map<String, Object> result = provider.constructParams(payload, requestId);

        final Map<String, Object> paramsExpected = new HashMap<>();
        paramsExpected.put("consolidationNumber", payload.getEmpConsolidationNumber());
        paramsExpected.put("variationScheduleItems", List.of("sch_abb_1", "sch_abb_2", 
				"sch_add_inf_1"));
        paramsExpected.put("reason", payload.getReasonRegulatorLed());
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(paramsExpected);
    }
}
