package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.lettertemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;


@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsApprovedDocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
    private EmpVariationUkEtsApprovedDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
        		DocumentTemplateGenerationContextActionType.EMP_VARIATION_ACCEPTED);
    }

    @Test
    void constructParams() {
    	EmpVariationUkEtsRequestPayload payload = EmpVariationUkEtsRequestPayload.builder()
    			.empConsolidationNumber(5)
                .determination(EmpVariationDetermination.builder()
                        .type(EmpVariationDeterminationType.APPROVED)
                        .reason("Reason")
                        .build())
                .empVariationDetailsReviewDecision(EmpVariationReviewDecision.builder()
	                		.details(EmpAcceptedVariationDecisionDetails.builder()
	                				.variationScheduleItems(List.of("sch_var_details_1", "sch_var_details_2"))
	                		.notes("notes")
	                		.build())
                		.build())
                .reviewGroupDecisions(Map.of(
                		EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision.builder()
	        				.type(EmpVariationReviewDecisionType.ACCEPTED)
	        				.details(EmpAcceptedVariationDecisionDetails.builder()
	        						.variationScheduleItems(List.of("sch_abbr_1", "sch_abbr_2")).build()).build(),
	        				EmpUkEtsReviewGroup.OPERATOR_DETAILS, EmpVariationReviewDecision.builder()
                				.type(EmpVariationReviewDecisionType.ACCEPTED)
                				.details(EmpAcceptedVariationDecisionDetails.builder()
                						.variationScheduleItems(List.of("sch_op_details_1")).build()).build(),
                				EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpVariationReviewDecision.builder()
                				.type(EmpVariationReviewDecisionType.REJECTED)
                				.details(ReviewDecisionDetails.builder().notes("notes").build()).build()
                		))
                .build();
    	String requestId = "1";
    	
        Map<String, Object> result = provider.constructParams(payload, requestId);

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
        		"empConsolidationNumber", 5,
        		"variationScheduleItems", List.of("sch_var_details_1", "sch_var_details_2", 
        				"sch_op_details_1", 
        				"sch_abbr_1", "sch_abbr_2")
        		));
    }
}
