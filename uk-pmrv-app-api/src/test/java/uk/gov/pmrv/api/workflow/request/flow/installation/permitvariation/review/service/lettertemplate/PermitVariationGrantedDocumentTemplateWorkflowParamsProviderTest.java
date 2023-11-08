package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.lettertemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;

@ExtendWith(MockitoExtension.class)
class PermitVariationGrantedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermitVariationGrantedDocumentTemplateWorkflowParamsProvider cut;

    @Test
    void getContextActionType() {
        assertThat(cut.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_GRANTED);
    }

    @Test
    void constructParams() {
    	LocalDate activationDate = LocalDate.now();
    	PermitVariationRequestPayload payload = PermitVariationRequestPayload.builder()
    			.permitConsolidationNumber(1)
                .determination(PermitVariationGrantDetermination.builder()
                        .type(DeterminationType.GRANTED)
                        .reason("Reason")
                        .activationDate(activationDate)
                        .build())
                .permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder()
	                		.details(PermitAcceptedVariationDecisionDetails.builder()
	                				.variationScheduleItems(List.of("sch_var_details_1", "sch_var_details_2"))
	                		.notes("notes")
	                		.build())
                		.build())
                .reviewGroupDecisions(Map.of(
                		PermitReviewGroup.ADDITIONAL_INFORMATION, PermitVariationReviewDecision.builder()
	        				.type(ReviewDecisionType.ACCEPTED)
	        				.details(PermitAcceptedVariationDecisionDetails.builder()
	        						.variationScheduleItems(List.of("sch_add_inf_1", "sch_add_inf_2")).build()).build(),
                		PermitReviewGroup.INSTALLATION_DETAILS, PermitVariationReviewDecision.builder()
                				.type(ReviewDecisionType.ACCEPTED)
                				.details(PermitAcceptedVariationDecisionDetails.builder()
                						.variationScheduleItems(List.of("sch_inst_details_1")).build()).build(),
                		PermitReviewGroup.CONFIDENTIALITY_STATEMENT, PermitVariationReviewDecision.builder()
                				.type(ReviewDecisionType.REJECTED)
                				.details(ReviewDecisionDetails.builder().notes("notes").build()).build()
                		))
                .build();
    	String requestId = "1";
    	
        Map<String, Object> result = cut.constructParams(payload, requestId);

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
        		"permitConsolidationNumber", 1,
        		"activationDate", activationDate,
        		"variationScheduleItems", List.of("sch_var_details_1", "sch_var_details_2", 
        				"sch_inst_details_1", 
        				"sch_add_inf_1", "sch_add_inf_2")
        		));
    }
}
