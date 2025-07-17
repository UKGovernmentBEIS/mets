package uk.gov.pmrv.api.workflow.request.application.taskview;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.netz.api.authorization.rules.services.resource.RequestTaskAuthorizationResourceService;
import uk.gov.netz.api.authorization.rules.services.resource.ResourceCriteria;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.user.application.UserServiceDelegator;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;

@ExtendWith(MockitoExtension.class)
class RequestTaskViewServiceTest {

    @InjectMocks
    private RequestTaskViewService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private UserServiceDelegator userServiceDelegator;

    @Mock
    private RequestTaskAuthorizationResourceService requestTaskAuthorizationResourceService;

    @Test
    void getTaskItemInfo() {
        final String user = "user";
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleTypeConstants.REGULATOR).build();
        final Long requestTaskId = 1L;
        final RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

        final Request request = createRequest("1", ca, accountId, requestType);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, appUser.getUserId(),
            "proceTaskId", requestTaskType);

        final UserDTO requestTaskAssigneeUser = RegulatorUserDTO.builder().firstName("fn").lastName("ln").build();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.ACCOUNT, "1",
                        ResourceType.CA, ca.name()))
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(userServiceDelegator.getUserById(requestTask.getAssignee())).thenReturn(requestTaskAssigneeUser);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria))
            .thenReturn(true);
        when(requestTaskAuthorizationResourceService.hasUserExecuteScopeOnRequestTaskType(appUser, requestTask.getType().name(), resourceCriteria))
            .thenReturn(true);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, appUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskType);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).isEqualTo(requestTaskType.getAllowedRequestTaskActionTypes());
        assertThat(result.isUserAssignCapable()).isTrue();
        assertThat(result.getRequestTask().getAssigneeUserId()).isEqualTo(user);
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(userServiceDelegator, times(1)).getUserById(requestTask.getAssignee());
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria);
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserExecuteScopeOnRequestTaskType(appUser, requestTaskType.name(), resourceCriteria);
    }

    @Test
    void getTaskItemInfo__user_has_not_assign_scope_on_request_tasks() {
        final String user = "user";
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleTypeConstants.REGULATOR).build();
        final Long requestTaskId = 1L;
        final RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

        final Request request = createRequest("1", ca, accountId, requestType);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, "another_user",
            "proceTaskId", requestTaskType);

        final UserDTO requestTaskAssigneeUser = RegulatorUserDTO.builder().firstName("fn").lastName("ln").build();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.ACCOUNT, "1",
                        ResourceType.CA, ca.name()))
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(userServiceDelegator.getUserById(requestTask.getAssignee())).thenReturn(requestTaskAssigneeUser);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria))
            .thenReturn(false);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, appUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskType);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).isEmpty();
        assertThat(result.isUserAssignCapable()).isFalse();
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(userServiceDelegator, times(1)).getUserById(requestTask.getAssignee());
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria);
        verifyNoMoreInteractions(requestTaskAuthorizationResourceService);
    }

    @Test
    void getTaskItemInfo_user_is_not_task_assignee() {
        final String user = "user";
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleTypeConstants.REGULATOR).build();
        final Long requestTaskId = 1L;
        final RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

        final Request request = createRequest("1", ca, accountId, requestType);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, "assignee",
            "proceTaskId", requestTaskType);

        final UserDTO requestTaskAssigneeUser = RegulatorUserDTO.builder().firstName("fn").lastName("ln").build();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.ACCOUNT, "1",
                        ResourceType.CA, ca.name()))
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(userServiceDelegator.getUserById(requestTask.getAssignee())).thenReturn(requestTaskAssigneeUser);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria))
            .thenReturn(true);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, appUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskType);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).isEmpty();
        assertThat(result.isUserAssignCapable()).isTrue();
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(userServiceDelegator, times(1)).getUserById(requestTask.getAssignee());
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria);
        verifyNoMoreInteractions(requestTaskAuthorizationResourceService);
    }

    @Test
    void getTaskItemInfo_user_has_not_execute_scope_on_request_task() {
        final String user = "user";
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleTypeConstants.REGULATOR).build();
        final Long requestTaskId = 1L;
        final RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

        final Request request = createRequest("1", ca, accountId, requestType);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, appUser.getUserId(),
            "proceTaskId", requestTaskType);

        final UserDTO requestTaskAssigneeUser = RegulatorUserDTO.builder().firstName("fn").lastName("ln").build();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.ACCOUNT, "1",
                        ResourceType.CA, ca.name()))
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(userServiceDelegator.getUserById(requestTask.getAssignee())).thenReturn(requestTaskAssigneeUser);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria))
            .thenReturn(true);
        when(requestTaskAuthorizationResourceService.hasUserExecuteScopeOnRequestTaskType(appUser, requestTask.getType().name(), resourceCriteria))
            .thenReturn(false);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, appUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskType);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).isEmpty();
        assertThat(result.isUserAssignCapable()).isTrue();
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(userServiceDelegator, times(1)).getUserById(requestTask.getAssignee());
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria);
        verify(requestTaskAuthorizationResourceService, times(1))
            .hasUserExecuteScopeOnRequestTaskType(appUser, requestTaskType.name(), resourceCriteria);
    }

    @Test
    void getTaskItemInfo_system_message() {
        final String user = "user";
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleTypeConstants.REGULATOR).build();
        final Long requestTaskId = 1L;
        final RequestType requestType = RequestType.SYSTEM_MESSAGE_NOTIFICATION;
        final RequestTaskType requestTaskType = RequestTaskType.VERIFIER_NO_LONGER_AVAILABLE;

        final Request request = createRequest("1", ca, accountId, requestType);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, appUser.getUserId(),
                "proceTaskId", requestTaskType);

        final UserDTO requestTaskAssigneeUser = RegulatorUserDTO.builder().firstName("fn").lastName("ln").build();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.ACCOUNT, "1",
                        ResourceType.CA, ca.name()))
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(userServiceDelegator.getUserById(requestTask.getAssignee())).thenReturn(requestTaskAssigneeUser);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria)).thenReturn(true);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, appUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskType);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).containsOnly(RequestTaskActionType.SYSTEM_MESSAGE_DISMISS);
        assertThat(result.isUserAssignCapable()).isTrue();
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(userServiceDelegator, times(1)).getUserById(requestTask.getAssignee());
        verify(requestTaskAuthorizationResourceService, times(1))
                .hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria);
        verify(requestTaskAuthorizationResourceService, never())
                .hasUserExecuteScopeOnRequestTaskType(appUser, requestTaskType.name(), resourceCriteria);
    }

    @Test
    public void getRequestTasks() {
        final String user = "user";
        final AppUser appUser = AppUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleTypeConstants.REGULATOR).build();
        Set<String> expectedResourceScopePermissions = Set.of(
            RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.toString(),
            RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW.toString(),
            RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_AMENDS.toString(),
            RequestTaskType.PERMIT_ISSUANCE_TRACK_PAYMENT.toString()
        );

        when(requestTaskAuthorizationResourceService.findRequestTaskTypesByRoleType(RoleTypeConstants.REGULATOR)).thenReturn(
            expectedResourceScopePermissions);

        Set<RequestTaskType> actualRequestTasks = service.getRequestTaskTypes(appUser.getRoleType());

        assertThat(actualRequestTasks.size()).isEqualTo(expectedResourceScopePermissions.size());
        assertThat(actualRequestTasks).containsAll(Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
            RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW, RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_AMENDS,
            RequestTaskType.PERMIT_ISSUANCE_TRACK_PAYMENT));
    }

    private Request createRequest(String requestId, CompetentAuthorityEnum ca,
        Long accountId, RequestType requestType) {
        return Request.builder()
            .id(requestId)
            .type(requestType)
            .competentAuthority(ca)
            .status(RequestStatus.IN_PROGRESS)
            .processInstanceId("procInst")
            .accountId(accountId)
            .creationDate(LocalDateTime.now())
            .build();
    }

    private RequestTask createRequestTask(Long requestTaskId, Request request, String assignee, String processTaskId,
        RequestTaskType requestTaskType) {
        return RequestTask.builder()
            .id(requestTaskId)
            .request(request)
            .processTaskId(processTaskId)
            .type(requestTaskType)
            .assignee(assignee)
            .dueDate(LocalDate.now().plusDays(14))
            .build();
    }
}
