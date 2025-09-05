package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAuthorityReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAuthorityResponseSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRSaveAuthorityResponseTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAcceptedRequestActionPayload;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ALRAuthorityResponseServiceTest {

    @InjectMocks
    private ALRAuthorityResponseService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void applyAuthorityResponseSaveAction() {
        final ALRAuthorityResponseSubmitRequestTaskPayload taskPayload = ALRAuthorityResponseSubmitRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        final ALRApplicationAuthorityReviewOutcome reviewOutcome = ALRApplicationAuthorityReviewOutcome.builder()
                .submissionDate(LocalDate.now())
                .build();
        final Map<String, Boolean> sectionsCompleted = Map.of("dateSubmittedToAuthority", true);
        final ALRSaveAuthorityResponseTaskActionPayload taskActionPaylod =
                ALRSaveAuthorityResponseTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.ALR_SAVE_AUTHORITY_RESPONSE_PAYLOAD)
                        .authorityReviewOutcome(reviewOutcome)
                        .authorityReviewSectionsCompleted(sectionsCompleted)
                        .build();

        // Invoke
        service.applyAuthorityResponseSaveAction(requestTask, taskActionPaylod);

        // Verify
        Assertions.assertEquals(taskPayload.getAuthorityReviewOutcome(), taskActionPaylod.getAuthorityReviewOutcome());
        Assertions.assertEquals(taskPayload.getAuthorityReviewSectionsCompleted(), taskActionPaylod.getAuthorityReviewSectionsCompleted());
    }

    @Test
    void authorityResponseNotifyOperator() {
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPaylod =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();

        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(ALRRequestPayload.builder()
                                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                                .build())
                        .build())
                .payload(ALRAuthorityResponseSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.ALR_AUTHORITY_RESPONSE_SUBMIT_PAYLOAD)
                        .authorityReviewOutcome(ALRApplicationAuthorityReviewOutcome.builder().submissionDate(LocalDate.now()).build())
                        .authorityReviewSectionsCompleted(sectionsCompleted)
                        .alrAttachments(attachments)
                        .build())
                .build();

        final ALRRequestPayload expectedPayload = ALRRequestPayload.builder()
                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                .decisionNotification(decisionNotification)
                .authorityReviewOutcome(ALRApplicationAuthorityReviewOutcome.builder().submissionDate(LocalDate.now()).build())
                .alrSectionsCompleted(sectionsCompleted)
                .alrAttachments(attachments)
                .build();

        // Invoke
        service.authorityResponseNotifyOperator(requestTask, taskActionPaylod);

        // Verify
        ALRRequestPayload updatedPayload = (ALRRequestPayload) requestTask.getRequest().getPayload();
        Assertions.assertEquals(expectedPayload, updatedPayload);
    }

    @Test
    void addSubmittedRequestAction() {
        final String requestId = "AEM";
        final String regulatorAssignee = "regulatorAssignee";

        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("regulatorUser")
                .build();
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");
        final FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("Activity_level_determination_approved_by_Authority_notice.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();



        final Request request = Request.builder()
                .id(requestId)
                .payload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .decisionNotification(decisionNotification)
                        .regulatorAssignee(regulatorAssignee)
                        .authorityReviewOutcome(ALRApplicationAuthorityReviewOutcome.builder().submissionDate(LocalDate.now()).build())
                        .alrAttachments(attachments)
                        .officialNotice(officialNotice)
                        .build())
                .build();

        final Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "operatorUser", RequestActionUserInfo.builder().name("operator1").roleCode("admin").build(),
                "regulatorUser", RequestActionUserInfo.builder().name("regulator").roleCode("admin").build()
        );

        final ALRApplicationAcceptedRequestActionPayload actionPayload =
                ALRApplicationAcceptedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.ALR_APPLICATION_ACCEPTED_PAYLOAD)
                        .authorityReviewOutcome(ALRApplicationAuthorityReviewOutcome.builder().submissionDate(LocalDate.now()).build())
                        .decisionNotification(decisionNotification)
                        .alrAttachments(attachments)
                        .usersInfo(usersInfo)
                        .officialNotice(officialNotice)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(Set.of("operatorUser"), "regulatorUser", request))
                .thenReturn(usersInfo);

        // Invoke
        service.addSubmittedRequestAction(requestId, RequestActionType.ALR_APPLICATION_ACCEPTED);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1))
                .getUsersInfo(Set.of("operatorUser"), "regulatorUser", request);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, RequestActionType.ALR_APPLICATION_ACCEPTED, regulatorAssignee);
    }
}
