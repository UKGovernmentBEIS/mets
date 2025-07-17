package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.ActivityLevelChangeInformation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalClosedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;

import java.time.Year;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalSubmitServiceTest {

    @InjectMocks
    private DoalSubmitService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void applySaveAction() {
        final DoalApplicationSubmitRequestTaskPayload taskPayload = DoalApplicationSubmitRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        final Doal doal = Doal.builder()
                .additionalDocuments(DoalAdditionalDocuments.builder()
                        .documents(Set.of(UUID.randomUUID()))
                        .comment("Comment")
                        .build())
                .build();
        final Map<String, Boolean> sectionsCompleted = Map.of("operatorActivityLevelReport", true);
        final DoalSaveApplicationRequestTaskActionPayload taskActionPaylod =
                DoalSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.DOAL_SAVE_APPLICATION_PAYLOAD)
                        .doal(doal)
                        .doalSectionsCompleted(sectionsCompleted)
                        .build();

        // Invoke
        service.applySaveAction(requestTask, taskActionPaylod);

        // Verify
        Assertions.assertEquals(taskPayload.getDoal(), taskActionPaylod.getDoal());
        Assertions.assertEquals(taskPayload.getDoalSectionsCompleted(), taskActionPaylod.getDoalSectionsCompleted());
    }

    @Test
    void notifyOperator() {
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPaylod =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();

        final Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder().year(Year.of(2022))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder().year(Year.of(2022))
                        .subInstallationName(SubInstallationName.AMMONIA)
                        .allowances(20)
                        .build(),
                PreliminaryAllocation.builder().year(Year.of(2023))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .allowances(10)
                        .build()
        );
        final Doal doal = Doal.builder()
                .determination(DoalClosedDetermination.builder()
                        .type(DoalDeterminationType.CLOSED)
                        .build())
                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                        .preliminaryAllocations(new TreeSet<>(allocations))
                        .build())
                .build();
        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(DoalRequestPayload.builder()
                                .payloadType(RequestPayloadType.DOAL_REQUEST_PAYLOAD)
                                .build())
                        .build())
                .payload(DoalApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                        .doal(doal)
                        .doalSectionsCompleted(sectionsCompleted)
                        .doalAttachments(attachments)
                        .build())
                .build();

        final DoalRequestPayload expectedPayload = DoalRequestPayload.builder()
                .payloadType(RequestPayloadType.DOAL_REQUEST_PAYLOAD)
                .decisionNotification(decisionNotification)
                .doal(doal)
                .doalSectionsCompleted(sectionsCompleted)
                .doalAttachments(attachments)
                .build();

        // Invoke
        service.notifyOperator(requestTask, taskActionPaylod);

        // Verify
        DoalRequestPayload updatedPayload = (DoalRequestPayload) requestTask.getRequest().getPayload();
        Assertions.assertEquals(expectedPayload, updatedPayload);
    }

    @Test
    void complete() {
        final Doal doal = Doal.builder()
                .determination(DoalClosedDetermination.builder()
                        .type(DoalDeterminationType.CLOSED)
                        .build())
                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder().build())
                .build();
        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(DoalRequestPayload.builder()
                                .payloadType(RequestPayloadType.DOAL_REQUEST_PAYLOAD)
                                .build())
                        .build())
                .payload(DoalApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                        .doal(doal)
                        .doalSectionsCompleted(sectionsCompleted)
                        .doalAttachments(attachments)
                        .build())
                .build();

        final DoalRequestPayload expectedPayload = DoalRequestPayload.builder()
                .payloadType(RequestPayloadType.DOAL_REQUEST_PAYLOAD)
                .doal(doal)
                .doalSectionsCompleted(sectionsCompleted)
                .doalAttachments(attachments)
                .build();

        // Invoke
        service.complete(requestTask);

        // Verify
        DoalRequestPayload updatedPayload = (DoalRequestPayload) requestTask.getRequest().getPayload();
        Assertions.assertEquals(expectedPayload, updatedPayload);
    }

    @Test
    void requestPeerReview() {
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId).build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final Doal doal = Doal.builder()
                .determination(DoalClosedDetermination.builder()
                        .type(DoalDeterminationType.CLOSED)
                        .build())
                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder().build())
                .build();
        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        Request request = Request.builder()
                .payload(DoalRequestPayload.builder()
                        .payloadType(RequestPayloadType.DOAL_REQUEST_PAYLOAD)
                        .build())
                .build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(DoalApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                        .doal(doal)
                        .doalSectionsCompleted(sectionsCompleted)
                        .doalAttachments(attachments)
                        .build())
                .build();

        final DoalRequestPayload expectedPayload = DoalRequestPayload.builder()
                .payloadType(RequestPayloadType.DOAL_REQUEST_PAYLOAD)
                .doal(doal)
                .doalSectionsCompleted(sectionsCompleted)
                .doalAttachments(attachments)
                .regulatorPeerReviewer(selectedPeerReviewer)
                .build();

        // Invoke
        service.requestPeerReview(requestTask, selectedPeerReviewer, user);

        // Verify
        DoalRequestPayload updatedPayload = (DoalRequestPayload) requestTask.getRequest().getPayload();
        Assertions.assertEquals(expectedPayload, updatedPayload);
        verify(requestService, times(1))
                .addActionToRequest(request, null, RequestActionType.DOAL_APPLICATION_PEER_REVIEW_REQUESTED, userId);
    }

    @Test
    void addProceededToAuthorityRequestAction() {
        final String requestId = "AEM";
        final Long accountId = 1L;
        final String regulatorAssignee = "regulatorAssignee";

        final Doal doal = Doal.builder()
                .determination(DoalProceedToAuthorityDetermination.builder()
                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                        .needsOfficialNotice(true)
                        .build())
                .build();
        final FileInfoDTO file = FileInfoDTO.builder()
                .name("Activity_level_determination_preliminary_allocation_letter.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
        final Year reportingYear = Year.of(2020);
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("regulatorUser")
                .build();
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");
        final Request request = Request.builder()
                .accountId(accountId)
                .payload(DoalRequestPayload.builder()
                        .regulatorAssignee(regulatorAssignee)
                        .reportingYear(reportingYear)
                        .doal(doal)
                        .decisionNotification(decisionNotification)
                        .officialNotice(file)
                        .doalAttachments(attachments)
                        .build())
                .build();

        final Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "operatorUser", RequestActionUserInfo.builder().name("operator1").roleCode("admin").build(),
                "regulatorUser", RequestActionUserInfo.builder().name("regulator").roleCode("admin").build()
        );

        final DoalApplicationProceededToAuthorityRequestActionPayload actionPayload =
                DoalApplicationProceededToAuthorityRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY_PAYLOAD)
                        .reportingYear(reportingYear)
                        .doal(doal)
                        .decisionNotification(decisionNotification)
                        .usersInfo(usersInfo)
                        .officialNotice(file)
                        .doalAttachments(attachments)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(Set.of("operatorUser"), "regulatorUser", request))
                .thenReturn(usersInfo);

        // Invoke
        service.addProceededToAuthorityRequestAction(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1))
                .getUsersInfo(Set.of("operatorUser"), "regulatorUser", request);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, RequestActionType.DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY, regulatorAssignee);
    }
}
