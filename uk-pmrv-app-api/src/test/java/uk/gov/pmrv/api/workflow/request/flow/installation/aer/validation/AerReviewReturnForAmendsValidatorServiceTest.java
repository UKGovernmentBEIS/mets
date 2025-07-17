package uk.gov.pmrv.api.workflow.request.flow.installation.aer.validation;

import org.junit.jupiter.api.Test;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AerReviewReturnForAmendsValidatorServiceTest {

    private final AerReviewReturnForAmendsValidatorService validatorService =
        new AerReviewReturnForAmendsValidatorService();

    @Test
    void validate() {
        AerApplicationReviewRequestTaskPayload taskPayload = AerApplicationReviewRequestTaskPayload.builder()
            .reviewGroupDecisions(Map.of(
                AerReviewGroup.FALLBACK, AerDataReviewDecision.builder()
                    .reviewDataType(AerReviewDataType.AER_DATA)
                    .type(AerDataReviewDecisionType.ACCEPTED)
                    .build(),
                AerReviewGroup.MEASUREMENT_CO2, AerDataReviewDecision.builder()
                    .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                    .reviewDataType(AerReviewDataType.AER_DATA)
                    .details(ChangesRequiredDecisionDetails.builder().build())
                    .build()
            ))
            .build();

        assertDoesNotThrow(() -> validatorService.validate(taskPayload));
    }

    @Test
    void validateThrowsExceptionWhenNoChangesRequiredDecisionDetails() {
        AerApplicationReviewRequestTaskPayload taskPayload = AerApplicationReviewRequestTaskPayload.builder()
            .reviewGroupDecisions(Map.of(
                AerReviewGroup.FALLBACK, AerDataReviewDecision.builder()
                    .reviewDataType(AerReviewDataType.AER_DATA)
                    .type(AerDataReviewDecisionType.ACCEPTED)
                    .build()
            ))
            .build();

        assertThrows(BusinessException.class, () -> validatorService.validate(taskPayload));
    }
}