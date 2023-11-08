package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaReviewReturnForAmendsValidatorServiceTest {

	
	@InjectMocks
    private EmpVariationCorsiaReviewReturnForAmendsValidatorService validator;

    @Test
    void validate_review_group_amends() {
    	UUID uuid = UUID.randomUUID();
    	Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpCorsiaReviewGroup.class);
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision.builder()
                .type(EmpVariationReviewDecisionType.REJECTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpVariationReviewDecision.builder()
                .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(ChangesRequiredDecisionDetails.builder().requiredChanges(List.of(
                        ReviewDecisionRequiredChange.builder().reason("reason").files(Set.of(uuid)).build())).build())
                .build());     
        
    	EmpVariationCorsiaApplicationReviewRequestTaskPayload payload = EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(buildEmp())
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        // Invoke
        assertDoesNotThrow(() -> validator.validate(payload));
    }
    
    @Test
    void validate_variation_details_amends() {
    	EmpVariationCorsiaApplicationReviewRequestTaskPayload payload = EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(buildEmp())
                .empVariationDetailsReviewDecision(EmpVariationReviewDecision.builder()
                        .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .details(ChangesRequiredDecisionDetails.builder().requiredChanges(List.of(
                                ReviewDecisionRequiredChange.builder().reason("reason").build())).build()).build())
                .build();

        // Invoke
        assertDoesNotThrow(() -> validator.validate(payload));
    }

    @Test
    void validate_no_amends() {
    	UUID uuid = UUID.randomUUID();
    	Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpCorsiaReviewGroup.class);
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision.builder()
                .type(EmpVariationReviewDecisionType.REJECTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpVariationReviewDecision.builder()
                .type(EmpVariationReviewDecisionType.ACCEPTED)
                .details(ChangesRequiredDecisionDetails.builder().requiredChanges(List.of(
                        ReviewDecisionRequiredChange.builder().reason("reason").files(Set.of(uuid)).build())).build())
                .build());     
        
    	EmpVariationCorsiaApplicationReviewRequestTaskPayload payload = EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(buildEmp())
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMP_VARIATION_REVIEW);
    }
    
    private EmissionsMonitoringPlanCorsia buildEmp() {
		return EmissionsMonitoringPlanCorsia.builder()
                .operatorDetails(EmpCorsiaOperatorDetails.builder()
                    .operatorName("name")
                    .build())
                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                .build();
	}
}
