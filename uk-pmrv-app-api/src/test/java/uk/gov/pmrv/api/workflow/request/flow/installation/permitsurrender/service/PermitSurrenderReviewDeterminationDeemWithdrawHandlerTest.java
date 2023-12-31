package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrender;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationDeemWithdraw;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewDeterminationDeemWithdrawHandler;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderReviewDeterminationDeemWithdrawHandlerTest {

    @InjectMocks
    private PermitSurrenderReviewDeterminationDeemWithdrawHandler handler;

    @Test
    void getType() {
        assertThat(handler.getType()).isEqualTo(PermitSurrenderReviewDeterminationType.DEEMED_WITHDRAWN);
    }

    @Test
    void handleDeterminationUponDecision() {
        PermitSurrenderReviewDetermination reviewDetermination = PermitSurrenderReviewDeterminationDeemWithdraw.builder()
            .type(PermitSurrenderReviewDeterminationType.DEEMED_WITHDRAWN)
            .build();

        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = PermitSurrenderApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
            .permitSurrender(PermitSurrender.builder().build())
            .reviewDecision(
                PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.ACCEPTED).details(ReviewDecisionDetails.builder().notes("notes")
                    .build()).build())
            .reviewDetermination(reviewDetermination)
            .build();

        PermitSurrenderReviewDecision newDecision = PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes")
                .build())
            .build();

        handler.handleDeterminationUponDecision(taskPayload, newDecision);
    }

    @Test
    void validateDecisionUponDetermination() {
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = PermitSurrenderApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
            .permitSurrender(PermitSurrender.builder().build())
            .build();

        handler.validateDecisionUponDetermination(taskPayload);
    }

    @Test
    void validateReview() {
        PermitSurrenderReviewDecision reviewDecision = PermitSurrenderReviewDecision.builder()
            .type(PermitSurrenderReviewDecisionType.REJECTED).details(ReviewDecisionDetails.builder().notes("new notes").build()).build();
        PermitSurrenderReviewDeterminationDeemWithdraw reviewDetermination = PermitSurrenderReviewDeterminationDeemWithdraw.builder()
            .type(PermitSurrenderReviewDeterminationType.DEEMED_WITHDRAWN)
            .build();

        handler.validateReview(reviewDecision, reviewDetermination);
    }

}
