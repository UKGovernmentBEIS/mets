package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBDetailsConfirmationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBDetailsConfirmationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class PermitTransferBReviewServiceTest {

    @InjectMocks
    private PermitTransferBReviewService service;

    @Mock
    private PermitReviewService permitReviewService;

    @Test
    void saveDetailsConfirmationReviewGroupDecision() {

        final PermitTransferBDetailsConfirmationReviewDecision decision =
            PermitTransferBDetailsConfirmationReviewDecision.builder()
                .type(PermitTransferBDetailsConfirmationReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build();
        final PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload actionPayload =
            PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload.builder()
                .decision(decision)
                .build();
        final PermitTransferBApplicationReviewRequestTaskPayload taskPayload =
            PermitTransferBApplicationReviewRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().type(RequestType.PERMIT_TRANSFER_B).build()).payload(taskPayload).build();

        service.saveDetailsConfirmationReviewGroupDecision(actionPayload, requestTask);

        verify(permitReviewService, times(1)).resetDeterminationIfNotValidWithDecisions(
            taskPayload, RequestType.PERMIT_TRANSFER_B
        );

        assertThat(taskPayload.getPermitTransferDetailsConfirmationDecision()).isEqualTo(decision);
    }

    @Test
    void saveRequestPeerReviewAction() {

        final String selectedPeerReviewer = "selectedPeerReviewer";
        final Long accountId = 1L;
        final String user = "user";
        final AppUser reviewer = AppUser.builder().userId(user).build();

        final PermitTransferBRequestPayload requestPayload = PermitTransferBRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_TRANSFER_B_REQUEST_PAYLOAD)
            .build();
        final Request request = Request.builder()
            .type(RequestType.PERMIT_TRANSFER_B)
            .accountId(accountId)
            .payload(requestPayload)
            .build();
        final PermitTransferBApplicationReviewRequestTaskPayload requestTaskPayload =
            PermitTransferBApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_REVIEW_PAYLOAD)
                .permit(Permit.builder()
                    .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                        .quantity(BigDecimal.TEN)
                        .build())
                    .build())
                .determination(
                    PermitIssuanceDeemedWithdrawnDetermination.builder().type(DeterminationType.DEEMED_WITHDRAWN)
                        .reason("reason").build())
                .reviewGroupDecisions(
                    Map.of(PermitReviewGroup.INSTALLATION_DETAILS, PermitIssuanceReviewDecision.builder().type(
                        ReviewDecisionType.ACCEPTED).build()))
                .permitTransferDetailsConfirmationDecision(PermitTransferBDetailsConfirmationReviewDecision.builder()
                    .details(ReviewDecisionDetails.builder().notes("notes").build()).build())
                .permitSectionsCompleted(Map.of("section1", List.of(true)))
                .reviewSectionsCompleted(Map.of("section1", true))
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_REVIEW)
            .request(request)
            .payload(requestTaskPayload)
            .build();

        service.saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, reviewer);

        final PermitTransferBRequestPayload updatedPayload =
            (PermitTransferBRequestPayload) request.getPayload();

        assertEquals(reviewer.getUserId(), updatedPayload.getRegulatorReviewer());
        assertEquals(selectedPeerReviewer, updatedPayload.getRegulatorPeerReviewer());
        assertThat(updatedPayload.getPermit()).isEqualTo(requestTaskPayload.getPermit());
        assertThat(updatedPayload.getPermitSectionsCompleted()).isEqualTo(
            requestTaskPayload.getPermitSectionsCompleted());
        assertThat(updatedPayload.getReviewSectionsCompleted()).isEqualTo(
            requestTaskPayload.getReviewSectionsCompleted());
        assertThat(updatedPayload.getReviewGroupDecisions()).isEqualTo(requestTaskPayload.getReviewGroupDecisions());
        assertThat(updatedPayload.getPermitTransferDetailsConfirmationDecision()).isEqualTo(
            requestTaskPayload.getPermitTransferDetailsConfirmationDecision());
        assertThat(updatedPayload.getDetermination()).isEqualTo(requestTaskPayload.getDetermination());
        assertThat(updatedPayload.getReviewAttachments()).isEmpty();
        assertThat(updatedPayload.getPermitAttachments()).isEmpty();
    }

    @Test
    void updatePermitTransferBRequestPayload() {

        final Long accountId = 1L;
        final String user = "user";
        final AppUser reviewer = AppUser.builder().userId(user).build();

        final PermitTransferBRequestPayload requestPayload = PermitTransferBRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_TRANSFER_B_REQUEST_PAYLOAD)
            .build();
        final Request request = Request.builder()
            .type(RequestType.PERMIT_TRANSFER_B)
            .accountId(accountId)
            .payload(requestPayload)
            .build();
        final PermitTransferBApplicationReviewRequestTaskPayload requestTaskPayload =
            PermitTransferBApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_REVIEW_PAYLOAD)
                .permit(Permit.builder()
                    .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                        .quantity(BigDecimal.TEN)
                        .build())
                    .build())
                .determination(
                    PermitIssuanceDeemedWithdrawnDetermination.builder().type(DeterminationType.DEEMED_WITHDRAWN)
                        .reason("reason").build())
                .reviewGroupDecisions(
                    Map.of(PermitReviewGroup.INSTALLATION_DETAILS, PermitIssuanceReviewDecision.builder().type(
                        ReviewDecisionType.ACCEPTED).build()))
                .permitTransferDetailsConfirmationDecision(PermitTransferBDetailsConfirmationReviewDecision.builder()
                    .details(ReviewDecisionDetails.builder().notes("notes").build()).build())
                .permitSectionsCompleted(Map.of("section1", List.of(true)))
                .reviewSectionsCompleted(Map.of("section1", true))
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_REVIEW)
            .request(request)
            .payload(requestTaskPayload)
            .build();

        service.updatePermitTransferBRequestPayload(requestTask, reviewer);

        final PermitTransferBRequestPayload updatedPayload =
            (PermitTransferBRequestPayload) request.getPayload();

        assertEquals(reviewer.getUserId(), updatedPayload.getRegulatorReviewer());
        assertThat(updatedPayload.getPermit()).isEqualTo(requestTaskPayload.getPermit());
        assertThat(updatedPayload.getPermitSectionsCompleted()).isEqualTo(
            requestTaskPayload.getPermitSectionsCompleted());
        assertThat(updatedPayload.getReviewSectionsCompleted()).isEqualTo(
            requestTaskPayload.getReviewSectionsCompleted());
        assertThat(updatedPayload.getReviewGroupDecisions()).isEqualTo(requestTaskPayload.getReviewGroupDecisions());
        assertThat(updatedPayload.getPermitTransferDetailsConfirmationDecision()).isEqualTo(
            requestTaskPayload.getPermitTransferDetailsConfirmationDecision());
        assertThat(updatedPayload.getDetermination()).isEqualTo(requestTaskPayload.getDetermination());
        assertThat(updatedPayload.getReviewAttachments()).isEmpty();
        assertThat(updatedPayload.getPermitAttachments()).isEmpty();
    }
}
