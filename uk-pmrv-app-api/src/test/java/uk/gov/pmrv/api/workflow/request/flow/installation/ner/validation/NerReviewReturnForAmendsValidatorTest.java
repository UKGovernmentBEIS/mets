package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerReviewGroupDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerReviewGroup;

@ExtendWith(MockitoExtension.class)
class NerReviewReturnForAmendsValidatorTest {

    @InjectMocks
    private NerReviewReturnForAmendsValidator validator;

    @Test
    void validate() {

        final NerApplicationReviewRequestTaskPayload taskPayload = NerApplicationReviewRequestTaskPayload.builder()
            .reviewGroupDecisions(Map.of(
                NerReviewGroup.ADDITIONAL_DOCUMENTS, NerReviewGroupDecision.builder()
                    .type(ReviewDecisionType.ACCEPTED)
                    .build(),
                NerReviewGroup.CONFIDENTIALITY_STATEMENT, NerReviewGroupDecision.builder()
                    .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                    .details(ChangesRequiredDecisionDetails.builder().build())
                    .build()
            ))
            .build();

        assertDoesNotThrow(() -> validator.validate(taskPayload));
    }

    @Test
    void validateThrowsExceptionWhenNoChangesRequiredDecisionDetails() {

        final NerApplicationReviewRequestTaskPayload taskPayload = NerApplicationReviewRequestTaskPayload.builder()
            .reviewGroupDecisions(Map.of(
                NerReviewGroup.ADDITIONAL_DOCUMENTS, NerReviewGroupDecision.builder()
                    .type(ReviewDecisionType.ACCEPTED)
                    .build(),
                NerReviewGroup.CONFIDENTIALITY_STATEMENT, NerReviewGroupDecision.builder()
                    .type(ReviewDecisionType.ACCEPTED)
                    .build()
            ))
            .build();

        assertThrows(BusinessException.class, () -> validator.validate(taskPayload));
    }
}