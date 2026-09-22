package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OriginalReviewerResolutionServiceTest {

    @InjectMocks
    private OriginalReviewerResolutionService cut;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void resolveAndOverrideSignatoryForPeerReview_overridesSignatory() {
        final Long taskId = 1L;
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory("current-signatory")
                .build();

        final RequestPayload requestPayload = org.mockito.Mockito.mock(RequestPayload.class);
        when(requestPayload.getRegulatorReviewer()).thenReturn("original-reviewer");

        final Request request = org.mockito.Mockito.mock(Request.class);
        when(request.getPayload()).thenReturn(requestPayload);

        final RequestTask requestTask = org.mockito.Mockito.mock(RequestTask.class);
        when(requestTask.getType()).thenReturn(RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW);
        when(requestTask.getRequest()).thenReturn(request);

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        cut.resolveAndOverrideSignatoryForPeerReview(taskId, decisionNotification);

        assertThat(decisionNotification.getSignatory()).isEqualTo("original-reviewer");
        verify(requestTaskService).findTaskById(taskId);
    }

    @Test
    void resolveAndOverrideSignatoryForPeerReview_nonPeerReviewKeepsSignatory() {
        final Long taskId = 1L;
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory("current-signatory")
                .build();

        final RequestTask requestTask = org.mockito.Mockito.mock(RequestTask.class);
        when(requestTask.getType()).thenReturn(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW);
        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        cut.resolveAndOverrideSignatoryForPeerReview(taskId, decisionNotification);

        assertThat(decisionNotification.getSignatory()).isEqualTo("current-signatory");
        verify(requestTask, never()).getRequest();
    }


    @Test
    void resolveAndOverrideSignatoryForPeerReview_nullDecisionNotification_doesNothing() {
        final Long taskId = 1L;
        cut.resolveAndOverrideSignatoryForPeerReview(taskId, null);
        org.mockito.Mockito.verifyNoInteractions(requestTaskService);
    }
}