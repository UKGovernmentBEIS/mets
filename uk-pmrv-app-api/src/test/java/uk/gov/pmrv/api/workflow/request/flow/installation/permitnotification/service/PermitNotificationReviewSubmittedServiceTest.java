package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.TemporaryChange;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationReviewSubmittedServiceTest {

    @InjectMocks
    private PermitNotificationReviewSubmittedService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Mock
    private PermitNotificationOfficialNoticeService noticeService;

    @Test
    void executeGrantedPostActions() {
        final String requestId = "1";
        Long accountId = 1L;
        DecisionNotification reviewDecisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("regulator1")
                .build();
        PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_NOTIFICATION_REQUEST_PAYLOAD)
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
                .reviewDecisionNotification(reviewDecisionNotification)
                .regulatorReviewer("regulatorReviewer")
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .accountId(accountId)
                .build();

        PermitNotificationReviewDecision expectedReviewDecision = PermitNotificationReviewDecision.builder()
                .type(requestPayload.getReviewDecision().getType())
                .details(
                        PermitNotificationAcceptedDecisionDetails.builder()
                                .notes("notes")
                                .officialNotice(((PermitNotificationAcceptedDecisionDetails) requestPayload.getReviewDecision().getDetails()).getOfficialNotice())
                                .followUp(((PermitNotificationAcceptedDecisionDetails) requestPayload.getReviewDecision().getDetails()).getFollowUp())
                                .build()
                )
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver
                .getUsersInfo(reviewDecisionNotification.getOperators(), reviewDecisionNotification.getSignatory(), request))
                .thenReturn(Map.of(
                        "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
                        "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
                ));

        // Invoke
        service.executeGrantedPostActions(requestId);

        // Verify
        verify(requestActionUserInfoResolver, times(1))
                .getUsersInfo(reviewDecisionNotification.getOperators(), reviewDecisionNotification.getSignatory(), request);
        verify(noticeService, times(1)).sendOfficialNotice(request);

        ArgumentCaptor<PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload>
                requestActionPayloadCaptor =
                ArgumentCaptor.forClass(PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload.class);
        verify(requestService, times(1)).addActionToRequest(Mockito.eq(request), requestActionPayloadCaptor.capture(),
                Mockito.eq(RequestActionType.PERMIT_NOTIFICATION_APPLICATION_GRANTED),
                Mockito.eq(requestPayload.getRegulatorReviewer()));

        PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload actionPayloadCaptured =
                requestActionPayloadCaptor.getValue();
        assertThat(actionPayloadCaptured.getPayloadType()).isEqualTo(
                RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_GRANTED_PAYLOAD);
        assertThat(actionPayloadCaptured.getUsersInfo()).isEqualTo(Map.of(
                "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
                "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
        ));
        assertThat(actionPayloadCaptured.getReviewDecisionNotification()).isEqualTo(
                requestPayload.getReviewDecisionNotification());
        assertThat(actionPayloadCaptured.getReviewDecision()).usingRecursiveComparison().isEqualTo(expectedReviewDecision);
    }

    @Test
    void executeRejectedPostActions() {
        final String requestId = "1";
        Long accountId = 1L;
        DecisionNotification reviewDecisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("regulator1")
                .build();
        PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_NOTIFICATION_REQUEST_PAYLOAD)
                .reviewDecision(PermitNotificationReviewDecision.builder()
                        .type(PermitNotificationReviewDecisionType.REJECTED)
                        .details(
                                PermitNotificationAcceptedDecisionDetails.builder()
                                        .notes("notes")
                                        .officialNotice("officialNotice")
                                        .followUp(FollowUp.builder()
                                                .followUpRequest("the request")
                                                .followUpResponseExpirationDate(LocalDate.of(2023, 1, 1))
                                                .build())
                                        .build()
                        )
                        .build())
                .reviewDecisionNotification(reviewDecisionNotification)
                .regulatorReviewer("regulatorReviewer")
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .accountId(accountId)
                .build();

        PermitNotificationReviewDecision expectedReviewDecision = PermitNotificationReviewDecision.builder()
                .type(requestPayload.getReviewDecision().getType())
                .details(
                        PermitNotificationAcceptedDecisionDetails.builder()
                                .notes("notes")
                                .officialNotice(((PermitNotificationAcceptedDecisionDetails) requestPayload.getReviewDecision().getDetails()).getOfficialNotice())
                                .followUp(FollowUp.builder()
                                        .followUpRequest("the request")
                                        .followUpResponseExpirationDate(LocalDate.of(2023, 1, 1))
                                        .build())
                                .build()
                )
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver
                .getUsersInfo(reviewDecisionNotification.getOperators(), reviewDecisionNotification.getSignatory(), request))
                .thenReturn(Map.of(
                        "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
                        "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
                ));

        // Invoke
        service.executeRejectedPostActions(requestId);

        // Verify
        verify(requestActionUserInfoResolver, times(1))
                .getUsersInfo(reviewDecisionNotification.getOperators(), reviewDecisionNotification.getSignatory(), request);
        verify(noticeService, times(1)).sendOfficialNotice(request);

        ArgumentCaptor<PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload>
                requestActionPayloadCaptor =
                ArgumentCaptor.forClass(PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload.class);
        verify(requestService, times(1)).addActionToRequest(Mockito.eq(request), requestActionPayloadCaptor.capture(),
                Mockito.eq(RequestActionType.PERMIT_NOTIFICATION_APPLICATION_REJECTED),
                Mockito.eq(requestPayload.getRegulatorReviewer()));

        PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload actionPayloadCaptured =
                requestActionPayloadCaptor.getValue();
        assertThat(actionPayloadCaptured.getPayloadType()).isEqualTo(
                RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_REJECTED_PAYLOAD);
        assertThat(actionPayloadCaptured.getUsersInfo()).isEqualTo(Map.of(
                "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
                "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
        ));
        assertThat(actionPayloadCaptured.getReviewDecisionNotification()).isEqualTo(
                requestPayload.getReviewDecisionNotification());
        assertThat(actionPayloadCaptured.getReviewDecision().getDetails()).usingRecursiveComparison().isEqualTo(expectedReviewDecision.getDetails());
    }

    @Test
    void executeFollowUpCompletedPostActions() {

        final String requestId = "1";
        final Long accountId = 1L;
        final UUID file = UUID.randomUUID();
        DecisionNotification followUpReviewDecisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("regulator1")
                .build();
        final PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_NOTIFICATION_REQUEST_PAYLOAD)
                .permitNotification(TemporaryChange.builder().type(PermitNotificationType.TEMPORARY_CHANGE).build())
                .reviewDecision(PermitNotificationReviewDecision.builder().type(PermitNotificationReviewDecisionType.ACCEPTED).details(
                                PermitNotificationAcceptedDecisionDetails.builder()
                                        .officialNotice("officialNotice")
                                        .followUp(FollowUp.builder()
                                                .followUpRequest("follow up request")
                                                .followUpResponseExpirationDate(LocalDate.of(2023, 1, 1))
                                                .build()
                                        )
                                        .build()
                        )
                        .build())
                .followUpResponse("follow up response")
                .followUpResponseFiles(Set.of(file))
                .followUpResponseAttachments(Map.of(file, "filename"))
                .followUpResponseSubmissionDate(LocalDate.of(2023, 1, 1))
                .followUpReviewDecision(PermitNotificationFollowUpReviewDecision.builder()
                        .type(PermitNotificationFollowUpReviewDecisionType.ACCEPTED)
                        .details(new ReviewDecisionDetails("notes"))
                        .build())
                .followUpReviewDecisionNotification(followUpReviewDecisionNotification)
                .regulatorReviewer("regulatorReviewer")
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .accountId(accountId)
                .build();

        final PermitNotificationFollowUpReviewDecision expectedReviewDecision =
                PermitNotificationFollowUpReviewDecision.builder()
                        .type(requestPayload.getFollowUpReviewDecision().getType())
                        .details(new ReviewDecisionDetails("notes"))
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver
                .getUsersInfo(followUpReviewDecisionNotification.getOperators(), followUpReviewDecisionNotification.getSignatory(), request))
                .thenReturn(Map.of(
                        "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
                        "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
                ));

        // Invoke
        service.executeFollowUpCompletedPostActions(requestId);

        // Verify
        verify(requestActionUserInfoResolver, times(1))
                .getUsersInfo(followUpReviewDecisionNotification.getOperators(), followUpReviewDecisionNotification.getSignatory(), request);
        verify(noticeService, times(1)).sendFollowUpOfficialNotice(request);

        final ArgumentCaptor<PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload> requestActionPayloadCaptor =
                ArgumentCaptor.forClass(PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload.class);
        verify(requestService, times(1)).addActionToRequest(Mockito.eq(request), requestActionPayloadCaptor.capture(),
                Mockito.eq(RequestActionType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED),
                Mockito.eq(requestPayload.getRegulatorReviewer()));

        final PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload actionPayloadCaptured =
                requestActionPayloadCaptor.getValue();
        assertThat(actionPayloadCaptured.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD);
        assertThat(actionPayloadCaptured.getRequest()).isEqualTo("follow up request");
        assertThat(actionPayloadCaptured.getResponse()).isEqualTo("follow up response");
        assertThat(actionPayloadCaptured.getResponseFiles()).isEqualTo(Set.of(file));
        assertThat(actionPayloadCaptured.getUsersInfo()).isEqualTo(Map.of(
                "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
                "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
        ));
        assertThat(actionPayloadCaptured.getReviewDecisionNotification()).isEqualTo(requestPayload.getFollowUpReviewDecisionNotification());
        assertThat(actionPayloadCaptured.getReviewDecision()).isEqualTo(expectedReviewDecision);
    }

    @Test
    void isFollowUpNeeded() {
        String requestId = "1";

        final PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder()
                .reviewDecision(PermitNotificationReviewDecision.builder().type(PermitNotificationReviewDecisionType.ACCEPTED).details(
                                PermitNotificationAcceptedDecisionDetails.builder()
                                        .followUp(FollowUp.builder()
                                                .followUpResponseRequired(true)
                                                .build()
                                        )
                                        .build()
                        )
                        .build())
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        assertThat(service.isFollowUpNeeded(requestId)).isTrue();

        verify(requestService, times(1)).findRequestById(requestId);
    }

}
