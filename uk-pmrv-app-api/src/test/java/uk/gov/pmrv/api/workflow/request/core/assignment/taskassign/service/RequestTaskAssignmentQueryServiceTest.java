package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.resource.RequestTaskAuthorizationResourceService;
import uk.gov.netz.api.authorization.rules.services.resource.ResourceCriteria;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.AssigneeUserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW;

@ExtendWith(MockitoExtension.class)
class RequestTaskAssignmentQueryServiceTest {

    @InjectMocks
    private RequestTaskAssignmentQueryService requestTaskAssignmentQueryService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;

    @Mock
    private RequestTaskAuthorizationResourceService requestTaskAuthorizationResourceService;

    @Test
    void getCandidateAssigneesByTaskId_non_peer_review_task() {
        final Long requestTaskId = 1L;
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = ENGLAND;
        final String userRole = OPERATOR;
        final String requestRegulatorReviewer = "requestRegulatorReviewer";
        Request request = Request.builder()
            .accountId(accountId)
            .competentAuthority(ca)
            .payload(PermitIssuanceRequestPayload.builder().regulatorReviewer(requestRegulatorReviewer).build())
            .build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId)
                .request(request).type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();
        AppUser user = AppUser.builder().roleType(userRole).build();
        List<String> candidateAssignees = List.of("userId1", "userId2");
        List<UserInfo> users = buildMockUserInfoList(candidateAssignees);
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = buildMockAssigneeUserInfoList(candidateAssignees);

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.ACCOUNT, "1",
                        ResourceType.CA, ca.name()))
                .build();

        // Mock
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
                requestTask.getType().name(), resourceCriteria, userRole)).thenReturn(candidateAssignees);
        when(userAuthService.getUsers(candidateAssignees)).thenReturn(users);

        // Invoke
        List<AssigneeUserInfoDTO> actualUsersInfo = requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(requestTaskId, user);

        // Assert
        assertEquals(candidateAssigneesInfo, actualUsersInfo);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTask);
        verify(requestTaskAuthorizationResourceService, times(1))
                .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTask.getType().name(), resourceCriteria, userRole);
        verify(userAuthService, times(1)).getUsers(candidateAssignees);
    }

    @Test
    void getCandidateAssigneesByTaskId_ordered_non_peer_review_task() {
        final Long requestTaskId = 1L;
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = ENGLAND;
        final String userRole = OPERATOR;
        final String requestRegulatorReviewer = "requestRegulatorReviewer";
        Request request = Request.builder()
            .accountId(accountId)
            .competentAuthority(ca)
            .payload(PermitIssuanceRequestPayload.builder().regulatorReviewer(requestRegulatorReviewer).build())
            .build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId)
            .request(request).type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();
        AppUser user = AppUser.builder().roleType(userRole).build();

        UserInfo userInfo1 = buildMockUserInfo("userId1", "aa", "aa");
        AssigneeUserInfoDTO assigneeUserInfoDTO1 = buildMockAssigneeUserInfo("userId1", "aa", "aa");
        UserInfo userInfo2 = buildMockUserInfo("userId2", "bb", "bb");
        AssigneeUserInfoDTO assigneeUserInfoDTO2 = buildMockAssigneeUserInfo("userId2", "bb", "bb");
        UserInfo userInfo3 = buildMockUserInfo("userId3", "BB", "Bc");
        AssigneeUserInfoDTO assigneeUserInfoDTO3 = buildMockAssigneeUserInfo("userId3", "BB", "Bc");
        UserInfo userInfo4 = buildMockUserInfo("userId4", "bb", "cc");
        AssigneeUserInfoDTO assigneeUserInfoDTO4 = buildMockAssigneeUserInfo("userId4", "bb", "cc");


        List<String> candidateAssignees = List.of("userId4", "userId3", "userId2", "userId1");
        List<UserInfo> users = List.of(userInfo4, userInfo3, userInfo2, userInfo1);
        List<AssigneeUserInfoDTO> orderedCandidateAssigneesInfo = List.of(assigneeUserInfoDTO1, assigneeUserInfoDTO2, assigneeUserInfoDTO3, assigneeUserInfoDTO4);

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.ACCOUNT, "1",
                        ResourceType.CA, ca.name()))
                .build();

        // Mock
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
            requestTask.getType().name(), resourceCriteria, userRole)).thenReturn(candidateAssignees);
        when(userAuthService.getUsers(candidateAssignees)).thenReturn(users);

        // Invoke
        List<AssigneeUserInfoDTO> actualUsersInfo = requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(requestTaskId, user);

        // Assert
        assertEquals(orderedCandidateAssigneesInfo, actualUsersInfo);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTask);
        verify(requestTaskAuthorizationResourceService, times(1))
            .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTask.getType().name(), resourceCriteria, userRole);
        verify(userAuthService, times(1)).getUsers(candidateAssignees);
    }

    @Test
    void getCandidateAssigneesByTaskId_peer_review_task() {
        final Long requestTaskId = 1L;
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = ENGLAND;
        final String userRole = REGULATOR;
        final String requestRegulatorReviewer = "requestRegulatorReviewer";
        final String userId1 = "userId1";
        final String userId2 = "userId2";
        Request request = Request.builder()
            .accountId(accountId)
            .competentAuthority(ca)
            .payload(PermitIssuanceRequestPayload.builder().regulatorReviewer(requestRegulatorReviewer).build())
            .build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId)
            .request(request).type(PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW).build();
        AppUser user = AppUser.builder().roleType(userRole).build();
        List<String> candidateAssignees = new ArrayList<>(Arrays.asList(requestRegulatorReviewer, userId1, userId2));
        List<String> peerReviewTaskCandidateAssignees = new ArrayList<>(Arrays.asList(userId1, userId2));
        List<UserInfo> users = buildMockUserInfoList(peerReviewTaskCandidateAssignees);
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = buildMockAssigneeUserInfoList(peerReviewTaskCandidateAssignees);

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.ACCOUNT, "1",
                        ResourceType.CA, ca.name()))
                .build();

        // Mock
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
            requestTask.getType().name(), resourceCriteria, userRole)).thenReturn(candidateAssignees);
        when(userAuthService.getUsers(candidateAssignees)).thenReturn(users);

        // Invoke
        List<AssigneeUserInfoDTO> actualUsersInfo = requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(requestTaskId, user);

        // Assert
        assertEquals(candidateAssigneesInfo, actualUsersInfo);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTask);
        verify(requestTaskAuthorizationResourceService, times(1))
            .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTask.getType().name(), resourceCriteria, userRole);
        verify(userAuthService, times(1)).getUsers(candidateAssignees);
    }

    @Test
    void getCandidateAssigneesByTaskId_not_valid_task_capability() {
        final Long requestTaskId = 1L;
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = ENGLAND;
        Request request = Request.builder().accountId(accountId).competentAuthority(ca).build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId)
                .request(request).type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();
        AppUser user = AppUser.builder().roleType(OPERATOR).build();

        // Mock
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        doThrow(new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED))
                .when(requestTaskAssignmentValidationService).validateTaskAssignmentCapability(requestTask);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(requestTaskId, user));

        // Assert
        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, businessException.getErrorCode());
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTask);
        verify(requestTaskAuthorizationResourceService, never())
                .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(anyString(), any(), any());
        verify(userAuthService, never()).getUsers(anyList());
    }

    @Test
    void getCandidateAssigneesByTaskId_empty_users() {
        final Long requestTaskId = 1L;
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = ENGLAND;
        Request request = Request.builder().accountId(accountId).competentAuthority(ca).build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId)
                .request(request).type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();
        AppUser user = AppUser.builder().roleType(OPERATOR).build();
        List<String> candidateAssignees = List.of();
        List<UserInfo> users = List.of();
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = List.of();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.ACCOUNT, "1",
                        ResourceType.CA, ca.name()))
                .build();

        // Mock
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
                requestTask.getType().name(), resourceCriteria, OPERATOR)).thenReturn(candidateAssignees);
        when(userAuthService.getUsers(candidateAssignees)).thenReturn(users);

        // Invoke
        List<AssigneeUserInfoDTO> actualUsersInfo = requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(requestTaskId, user);

        // Assert
        assertEquals(candidateAssigneesInfo, actualUsersInfo);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTask);
        verify(requestTaskAuthorizationResourceService, times(1))
                .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTask.getType().name(), resourceCriteria, OPERATOR);
        verify(userAuthService, times(1)).getUsers(candidateAssignees);
    }

    @Test
    void getCandidateAssigneesByTaskType_peer_review_task() {
        final RequestTaskType requestTaskType = RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW;
        final CompetentAuthorityEnum ca = ENGLAND;
        final String appUserRole = REGULATOR;
        final String appUserId = "userId";
        final String userId1 = "userId1";
        final String userId2 = "userId2";
        AppAuthority pmrvAuthority = AppAuthority.builder()
            .competentAuthority(ca)
            .build();
        AppUser appUser = AppUser.builder().userId(appUserId).roleType(appUserRole).authorities(List.of(pmrvAuthority)).build();
        List<String> candidateAssignees = new ArrayList<>(Arrays.asList(appUserId, userId1, userId2));
        List<String> peerReviewTaskCandidateAssignees = new ArrayList<>(Arrays.asList(userId1, userId2));
        List<UserInfo> users = buildMockUserInfoList(peerReviewTaskCandidateAssignees);
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = buildMockAssigneeUserInfoList(peerReviewTaskCandidateAssignees);

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.CA, ca.name()))
                .build();
        // Mock
        when(requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
            requestTaskType.name(), resourceCriteria, appUserRole)).thenReturn(candidateAssignees);
        when(userAuthService.getUsers(candidateAssignees)).thenReturn(users);

        // Invoke
        List<AssigneeUserInfoDTO> actualUsersInfo = requestTaskAssignmentQueryService
            .getCandidateAssigneesByTaskType(requestTaskType, appUser);

        // Assert
        assertEquals(candidateAssigneesInfo, actualUsersInfo);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTaskType);
        verify(requestTaskAuthorizationResourceService, times(1))
            .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType.name(), resourceCriteria, appUserRole);
        verify(userAuthService, times(1)).getUsers(candidateAssignees);
    }

    @Test
    void getCandidateAssigneesByTaskType_non_peer_review_task() {
        final RequestTaskType requestTaskType = RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW;
        final CompetentAuthorityEnum ca = ENGLAND;
        final String appUserRole = REGULATOR;
        final String appUserId = "userId";
        final String userId1 = "userId1";
        final String userId2 = "userId2";
        AppAuthority pmrvAuthority = AppAuthority.builder()
            .competentAuthority(ca)
            .build();
        AppUser appUser = AppUser.builder().userId(appUserId).roleType(appUserRole).authorities(List.of(pmrvAuthority)).build();
        List<String> candidateAssignees = new ArrayList<>(Arrays.asList(appUserId, userId1, userId2));
        List<UserInfo> users = buildMockUserInfoList(candidateAssignees);
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = buildMockAssigneeUserInfoList(candidateAssignees);

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.CA, ca.name()))
                .build();
        // Mock
        when(requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
            requestTaskType.name(), resourceCriteria, appUserRole)).thenReturn(candidateAssignees);
        when(userAuthService.getUsers(candidateAssignees)).thenReturn(users);

        // Invoke
        List<AssigneeUserInfoDTO> actualUsersInfo = requestTaskAssignmentQueryService
            .getCandidateAssigneesByTaskType(requestTaskType, appUser);

        // Assert
        assertEquals(candidateAssigneesInfo, actualUsersInfo);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTaskType);
        verify(requestTaskAuthorizationResourceService, times(1))
            .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType.name(), resourceCriteria, appUserRole);
        verify(userAuthService, times(1)).getUsers(candidateAssignees);
    }

    @Test
    void getCandidateAssigneesByTaskType_task_type_not_assignable() {
        final RequestTaskType requestTaskType = RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW;
        final String appUserId = "userId";
        AppUser appUser = AppUser.builder().userId(appUserId).build();

        // Mock
        doThrow(new BusinessException(ErrorCode.REQUEST_TASK_NOT_ASSIGNABLE))
            .when(requestTaskAssignmentValidationService).validateTaskAssignmentCapability(requestTaskType);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
            requestTaskAssignmentQueryService.getCandidateAssigneesByTaskType(requestTaskType, appUser));

        // Assert
        assertEquals(ErrorCode.REQUEST_TASK_NOT_ASSIGNABLE, businessException.getErrorCode());
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTaskType);
        verifyNoInteractions(requestTaskAuthorizationResourceService, userAuthService);
    }

    private List<UserInfo> buildMockUserInfoList(List<String> userIds) {
        return userIds.stream()
                .map(userId -> buildMockUserInfo(userId, userId, userId))
                .collect(Collectors.toList());
    }

    private List<AssigneeUserInfoDTO> buildMockAssigneeUserInfoList(List<String> userIds) {
        return userIds.stream()
                .map(userId -> buildMockAssigneeUserInfo(userId, userId, userId))
                .collect(Collectors.toList());
    }

    private UserInfo buildMockUserInfo(String userId, String firstName, String lastName) {
        return UserInfo.builder().id(userId).firstName(firstName).lastName(lastName).build();
    }

    private AssigneeUserInfoDTO buildMockAssigneeUserInfo(String userId, String firstName, String lastName) {
        return AssigneeUserInfoDTO.builder().id(userId).firstName(firstName).lastName(lastName).build();
    }
}
