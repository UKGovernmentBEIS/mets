package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentStatus;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalAuthorityResponseServiceTest {

    @InjectMocks
    private DoalAuthorityResponseService service;

    @Mock
    private RequestService requestService;

    @Mock
    private AccountFileAttachmentService accountFileAttachmentService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void applyAuthorityResponseSaveAction() {
        final DoalAuthorityResponseRequestTaskPayload taskPayload = DoalAuthorityResponseRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        final DoalAuthority doalAuthority = DoalAuthority.builder()
                .dateSubmittedToAuthority(DateSubmittedToAuthority.builder()
                        .date(LocalDate.now())
                        .build())
                .build();
        final Map<String, Boolean> sectionsCompleted = Map.of("dateSubmittedToAuthority", true);
        final DoalSaveAuthorityResponseTaskActionPayload taskActionPaylod =
                DoalSaveAuthorityResponseTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.DOAL_SAVE_AUTHORITY_RESPONSE_PAYLOAD)
                        .doalAuthority(doalAuthority)
                        .doalSectionsCompleted(sectionsCompleted)
                        .build();

        // Invoke
        service.applyAuthorityResponseSaveAction(requestTask, taskActionPaylod);

        // Verify
        Assertions.assertEquals(taskPayload.getDoalAuthority(), taskActionPaylod.getDoalAuthority());
        Assertions.assertEquals(taskPayload.getDoalSectionsCompleted(), taskActionPaylod.getDoalSectionsCompleted());
    }

    @Test
    void authorityResponseNotifyOperator_validResponse_updatesPayloadAndFinalizesAttachments() {
        // given
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();

        NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();

        DoalGrantAuthorityResponse authorityResponse = DoalGrantAuthorityResponse.builder()
                .type(DoalAuthorityResponseType.VALID)
                .build();

        DoalAuthority doalAuthority = DoalAuthority.builder()
                .dateSubmittedToAuthority(DateSubmittedToAuthority.builder()
                        .date(LocalDate.now())
                        .build())
                .authorityResponse(authorityResponse)
                .build();

        Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(DoalRequestPayload.builder()
                                .payloadType(RequestPayloadType.DOAL_REQUEST_PAYLOAD)
                                .build())
                        .accountId(1L)
                        .build())
                .payload(DoalAuthorityResponseRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.DOAL_AUTHORITY_RESPONSE_PAYLOAD)
                        .doalAuthority(doalAuthority)
                        .doalSectionsCompleted(sectionsCompleted)
                        .doalAttachments(attachments)
                        .build())
                .build();

        // when
        service.authorityResponseNotifyOperator(requestTask, taskActionPayload);

        // then – payload mutation
        DoalRequestPayload updatedPayload =
                (DoalRequestPayload) requestTask.getRequest().getPayload();

        assertThat(updatedPayload.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(updatedPayload.getDoalAuthority()).isEqualTo(doalAuthority);
        assertThat(updatedPayload.getDoalSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(updatedPayload.getDoalAttachments()).isEqualTo(attachments);

        // then – side effect
        verify(accountFileAttachmentService)
                .updateAccountFileAttachmentsStatusByAccountId(
                        AccountFileAttachmentWorkflow.DOAL,
                        AccountFileAttachmentStatus.FINALIZED,
                        1L
                );
    }

    @Test
    void addSubmittedRequestAction() {
        final String requestId = "AEM";
        final String regulatorAssignee = "regulatorAssignee";
        final DoalAuthority doalAuthority = DoalAuthority.builder()
                .dateSubmittedToAuthority(DateSubmittedToAuthority.builder()
                        .date(LocalDate.now())
                        .build())
                .build();
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
                .payload(DoalRequestPayload.builder()
                        .payloadType(RequestPayloadType.DOAL_REQUEST_PAYLOAD)
                        .regulatorAssignee(regulatorAssignee)
                        .doalAuthority(doalAuthority)
                        .decisionNotification(decisionNotification)
                        .doalAttachments(attachments)
                        .officialNotice(officialNotice)
                        .build())
                .build();

        final Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "operatorUser", RequestActionUserInfo.builder().name("operator1").roleCode("admin").build(),
                "regulatorUser", RequestActionUserInfo.builder().name("regulator").roleCode("admin").build()
        );

        final DoalApplicationAcceptedRequestActionPayload actionPayload =
                DoalApplicationAcceptedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.DOAL_APPLICATION_ACCEPTED_PAYLOAD)
                        .doalAuthority(doalAuthority)
                        .decisionNotification(decisionNotification)
                        .doalAttachments(attachments)
                        .usersInfo(usersInfo)
                        .officialNotice(officialNotice)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(Set.of("operatorUser"), "regulatorUser", request))
                .thenReturn(usersInfo);

        // Invoke
        service.addSubmittedRequestAction(requestId, RequestActionType.DOAL_APPLICATION_ACCEPTED);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1))
                .getUsersInfo(Set.of("operatorUser"), "regulatorUser", request);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, RequestActionType.DOAL_APPLICATION_ACCEPTED, regulatorAssignee);
    }
}
