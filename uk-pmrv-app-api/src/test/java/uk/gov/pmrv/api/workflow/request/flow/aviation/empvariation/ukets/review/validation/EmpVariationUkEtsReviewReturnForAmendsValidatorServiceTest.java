package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsReviewReturnForAmendsValidatorServiceTest {

	
	@InjectMocks
    private EmpVariationUkEtsReviewReturnForAmendsValidatorService validator;

    @Test
    void validate_review_group_amends() {
    	UUID uuid = UUID.randomUUID();
    	Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision.builder()
                .type(EmpVariationReviewDecisionType.REJECTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpVariationReviewDecision.builder()
                .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(ChangesRequiredDecisionDetails.builder().requiredChanges(List.of(
                        ReviewDecisionRequiredChange.builder().reason("reason").files(Set.of(uuid)).build())).build())
                .build());     
        
    	EmpVariationUkEtsApplicationReviewRequestTaskPayload payload = EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(buildEmp())
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        // Invoke
        assertDoesNotThrow(() -> validator.validate(payload));
    }
    
    @Test
    void validate_variation_details_amends() {
    	EmpVariationUkEtsApplicationReviewRequestTaskPayload payload = EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
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
    	Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision.builder()
                .type(EmpVariationReviewDecisionType.REJECTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpVariationReviewDecision.builder()
                .type(EmpVariationReviewDecisionType.ACCEPTED)
                .details(ChangesRequiredDecisionDetails.builder().requiredChanges(List.of(
                        ReviewDecisionRequiredChange.builder().reason("reason").files(Set.of(uuid)).build())).build())
                .build());     
        
    	EmpVariationUkEtsApplicationReviewRequestTaskPayload payload = EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(buildEmp())
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validate(payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMP_VARIATION_REVIEW);
    }
    
    private EmissionsMonitoringPlanUkEts buildEmp() {
		return EmissionsMonitoringPlanUkEts.builder()
                .operatorDetails(EmpOperatorDetails.builder()
                    .operatorName("name")
                    .crcoCode("code")
                    .build())
                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                .build();
	}
}
