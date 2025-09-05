package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETICompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HSETICompleteServiceTest {

    @InjectMocks
    private HSETICompleteService completeService;

    @Mock
    private RequestService requestService;

    @Mock
    private  RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    public void addApprovedRequestAction() {
        final String requestId = "HSETI00177-2021_2025";
        final Long accountId = 1L;
        UUID attachmentId = UUID.randomUUID();
        UUID attachmentId1 = UUID.randomUUID();


        DecisionNotification decisionNotification = DecisionNotification
                .builder()
                .signatory("sig")
                .operators(Set.of("op1", "op2"))
                .build();

        HSETIRequestPayload requestPayload = HSETIRequestPayload
                .builder()
                .hsetiAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewer("test")
                .decisionNotification(decisionNotification)
                .regulatorReviewAttachments(Map.of(attachmentId1,"test"))
                .build();

        Request request = Request
                .builder()
                .payload(requestPayload)
                .id(requestId)
                .accountId(accountId)
                .type(RequestType.HSE_TI)
                .build();


        Map<String, RequestActionUserInfo> userInfos = new HashMap<>();

        userInfos.put("op1", RequestActionUserInfo.builder().name("operator 1").roleCode("role 1").build());
        userInfos.put("op2", RequestActionUserInfo.builder().name("operator 2").roleCode("role2").build());

        HSETICompletedRequestActionPayload actionPayload = HSETICompletedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.HSE_TI_COMPLETED_PAYLOAD)
                .hsetiAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewAttachments(Map.of(attachmentId1,"test"))
                .decisionNotification(decisionNotification)
                .usersInfo(userInfos)
                .build();


        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(Set.of("op1","op2"), "sig", request)).thenReturn(userInfos);

        completeService.addApprovedRequestAction(requestId);

        verify(requestService, times(1)).addActionToRequest(request,actionPayload, RequestActionType.HSE_TI_APPROVED, requestPayload.getRegulatorReviewer());

    }

    @Test
    public void addRejectedRequestAction() {
        final String requestId = "HSETI00177-2021_2025";
        final Long accountId = 1L;
        UUID attachmentId = UUID.randomUUID();
        UUID attachmentId1 = UUID.randomUUID();


        DecisionNotification decisionNotification = DecisionNotification
                .builder()
                .signatory("sig")
                .operators(Set.of("op1", "op2"))
                .build();

        HSETIRequestPayload requestPayload = HSETIRequestPayload
                .builder()
                .hsetiAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewer("test")
                .decisionNotification(decisionNotification)
                .regulatorReviewAttachments(Map.of(attachmentId1,"test"))
                .build();

        Request request = Request
                .builder()
                .payload(requestPayload)
                .id(requestId)
                .accountId(accountId)
                .type(RequestType.HSE_TI)
                .build();


        Map<String, RequestActionUserInfo> userInfos = new HashMap<>();

        userInfos.put("op1", RequestActionUserInfo.builder().name("operator 1").roleCode("role 1").build());
        userInfos.put("op2", RequestActionUserInfo.builder().name("operator 2").roleCode("role2").build());

        HSETICompletedRequestActionPayload actionPayload = HSETICompletedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.HSE_TI_COMPLETED_PAYLOAD)
                .hsetiAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewAttachments(Map.of(attachmentId1,"test"))
                .decisionNotification(decisionNotification)
                .usersInfo(userInfos)
                .build();


        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(Set.of("op1","op2"), "sig", request)).thenReturn(userInfos);

        completeService.addRejectedRequestAction(requestId);

        verify(requestService, times(1)).addActionToRequest(request,actionPayload, RequestActionType.HSE_TI_REJECTED, requestPayload.getRegulatorReviewer());
    }

    @Test
    public void addWithdrawnRequestAction() {
        final String requestId = "HSETI00177-2021_2025";
        final Long accountId = 1L;
        UUID attachmentId = UUID.randomUUID();
        UUID attachmentId1 = UUID.randomUUID();


        DecisionNotification decisionNotification = DecisionNotification
                .builder()
                .signatory("sig")
                .operators(Set.of("op1", "op2"))
                .build();

        HSETIRequestPayload requestPayload = HSETIRequestPayload
                .builder()
                .hsetiAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewer("test")
                .decisionNotification(decisionNotification)
                .regulatorReviewAttachments(Map.of(attachmentId1,"test"))
                .build();

        Request request = Request
                .builder()
                .payload(requestPayload)
                .id(requestId)
                .accountId(accountId)
                .type(RequestType.HSE_TI)
                .build();


        Map<String, RequestActionUserInfo> userInfos = new HashMap<>();

        userInfos.put("op1", RequestActionUserInfo.builder().name("operator 1").roleCode("role 1").build());
        userInfos.put("op2", RequestActionUserInfo.builder().name("operator 2").roleCode("role2").build());

        HSETICompletedRequestActionPayload actionPayload = HSETICompletedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.HSE_TI_COMPLETED_PAYLOAD)
                .hsetiAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewAttachments(Map.of(attachmentId1,"test"))
                .decisionNotification(decisionNotification)
                .usersInfo(userInfos)
                .build();


        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(Set.of("op1","op2"), "sig", request)).thenReturn(userInfos);

        completeService.addWithdrawnRequestAction(requestId);

        verify(requestService, times(1)).addActionToRequest(request,actionPayload, RequestActionType.HSE_TI_WITHDRAWN, requestPayload.getRegulatorReviewer());
    }

    @Test
    public void addDeemedWithdrawnRequestAction() {
        final String requestId = "HSETI00177-2021_2025";
        final Long accountId = 1L;
        UUID attachmentId = UUID.randomUUID();
        UUID attachmentId1 = UUID.randomUUID();


        DecisionNotification decisionNotification = DecisionNotification
                .builder()
                .signatory("sig")
                .operators(Set.of("op1", "op2"))
                .build();

        HSETIRequestPayload requestPayload = HSETIRequestPayload
                .builder()
                .hsetiAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewer("test")
                .decisionNotification(decisionNotification)
                .regulatorReviewAttachments(Map.of(attachmentId1,"test"))
                .build();

        Request request = Request
                .builder()
                .payload(requestPayload)
                .id(requestId)
                .accountId(accountId)
                .type(RequestType.HSE_TI)
                .build();


        Map<String, RequestActionUserInfo> userInfos = new HashMap<>();

        userInfos.put("op1", RequestActionUserInfo.builder().name("operator 1").roleCode("role 1").build());
        userInfos.put("op2", RequestActionUserInfo.builder().name("operator 2").roleCode("role2").build());

        HSETICompletedRequestActionPayload actionPayload = HSETICompletedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.HSE_TI_COMPLETED_PAYLOAD)
                .hsetiAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewAttachments(Map.of(attachmentId1,"test"))
                .decisionNotification(decisionNotification)
                .usersInfo(userInfos)
                .build();


        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(Set.of("op1","op2"), "sig", request)).thenReturn(userInfos);

        completeService.addDeemedWithdrawnRequestAction(requestId);

        verify(requestService, times(1)).addActionToRequest(request,actionPayload, RequestActionType.HSE_TI_DEEMED_WITHDRAWN, requestPayload.getRegulatorReviewer());
    }
}
