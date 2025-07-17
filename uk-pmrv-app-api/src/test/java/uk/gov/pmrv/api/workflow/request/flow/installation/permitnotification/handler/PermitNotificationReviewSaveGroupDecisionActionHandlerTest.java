package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.RequestPermitNotificationReviewService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationReviewSaveGroupDecisionActionHandlerTest {

    @InjectMocks
    private PermitNotificationReviewSaveGroupDecisionActionHandler handler;

    @Mock
    private RequestPermitNotificationReviewService requestPermitNotificationReviewService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        final Long requestTaskId = 1L;
        final RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION;
        final AppUser appUser = AppUser.builder().userId("user").build();
        final PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload payload = PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .reviewDecision(PermitNotificationReviewDecision.builder()
                        .type(PermitNotificationReviewDecisionType.ACCEPTED)
                        .details(
                            PermitNotificationAcceptedDecisionDetails.builder()
                                .notes("notes")
                                .officialNotice("officialNotice")
                                .followUp(FollowUp.builder()
                                    .followUpResponseRequired(false)
                                    .build())
                                .build()
                        )
                        .build())
                .reviewDeterminationCompleted(true)
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).processTaskId("processTaskId").build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(requestPermitNotificationReviewService, times(1)).saveReviewDecision(payload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION);
    }
}
