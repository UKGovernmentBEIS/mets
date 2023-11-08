package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrender;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewDeterminationGrantHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewDeterminationHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewDeterminationHandlerService;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderReviewDeterminationHandlerServiceTest {

    @InjectMocks
    private PermitSurrenderReviewDeterminationHandlerService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitSurrenderReviewDeterminationGrantHandler permitSurrenderReviewDeterminationGrantedHandler;

    @Spy
    private ArrayList<PermitSurrenderReviewDeterminationHandler> permitSurrenderReviewDeterminationHandler;

    @BeforeEach
    public void setUp() {
        permitSurrenderReviewDeterminationHandler.add(permitSurrenderReviewDeterminationGrantedHandler);
    }

    @Test
    void handleDeterminationUponDecision() {
        PermitSurrenderReviewDeterminationType determinationType = PermitSurrenderReviewDeterminationType.GRANTED;
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload =
            PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                .permitSurrender(PermitSurrender.builder().build())
                .build();

        PermitSurrenderReviewDecision decision = PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.ACCEPTED).details(
            new ReviewDecisionDetails("notes")).build();

        when(permitSurrenderReviewDeterminationGrantedHandler.getType()).thenReturn(determinationType);

        service.handleDeterminationUponDecision(determinationType, taskPayload, decision);

        verify(permitSurrenderReviewDeterminationGrantedHandler, times(1)).getType();
        verify(permitSurrenderReviewDeterminationGrantedHandler, times(1)).handleDeterminationUponDecision(taskPayload, decision);
    }

    @Test
    void validateDecisionUponDetermination() {
        PermitSurrenderReviewDeterminationType determinationType = PermitSurrenderReviewDeterminationType.GRANTED;
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload =
            PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                .permitSurrender(PermitSurrender.builder().build())
                .build();

        PermitSurrenderReviewDetermination reviewDetermination = PermitSurrenderReviewDeterminationGrant.builder().type(determinationType)
            .stopDate(LocalDate.now().minusDays(1)).build();

        when(permitSurrenderReviewDeterminationGrantedHandler.getType()).thenReturn(determinationType);

        service.validateDecisionUponDetermination(taskPayload, reviewDetermination);

        verify(permitSurrenderReviewDeterminationGrantedHandler, times(1)).getType();
        verify(permitSurrenderReviewDeterminationGrantedHandler, times(1)).validateDecisionUponDetermination(taskPayload);
    }

    @Test
    void validateDecisionUponDetermination_no_handler_found() {
        PermitSurrenderReviewDeterminationType determinationType = PermitSurrenderReviewDeterminationType.GRANTED;
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload =
            PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                .permitSurrender(PermitSurrender.builder().build())
                .build();

        PermitSurrenderReviewDetermination reviewDetermination = PermitSurrenderReviewDeterminationGrant.builder().type(determinationType)
            .stopDate(LocalDate.now().minusDays(1)).build();

        when(permitSurrenderReviewDeterminationGrantedHandler.getType()).thenReturn(PermitSurrenderReviewDeterminationType.REJECTED);

        assertThrows(RuntimeException.class, () -> service.validateDecisionUponDetermination(taskPayload, reviewDetermination));

        verify(permitSurrenderReviewDeterminationGrantedHandler, times(1)).getType();
        verify(permitSurrenderReviewDeterminationGrantedHandler, never()).validateDecisionUponDetermination(Mockito.any());
    }

    @Test
    void validateReview() {
        PermitSurrenderReviewDeterminationType determinationType = PermitSurrenderReviewDeterminationType.GRANTED;

        PermitSurrenderReviewDeterminationGrant reviewDetermination = PermitSurrenderReviewDeterminationGrant.builder().type(determinationType)
            .stopDate(LocalDate.now().minusDays(1)).build();
        PermitSurrenderReviewDecision reviewDecision = PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.ACCEPTED)
            .details(new ReviewDecisionDetails("notes")).build();

        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload =
            PermitSurrenderApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD)
                .permitSurrender(PermitSurrender.builder().build())
                .reviewDecision(reviewDecision)
                .reviewDetermination(reviewDetermination)
                .build();

        when(permitSurrenderReviewDeterminationGrantedHandler.getType()).thenReturn(determinationType);

        service.validateReview(taskPayload.getReviewDecision(), taskPayload.getReviewDetermination());

        verify(permitSurrenderReviewDeterminationGrantedHandler, times(1)).getType();
        verify(permitSurrenderReviewDeterminationGrantedHandler, times(1)).validateReview(reviewDecision, reviewDetermination);
    }
}
