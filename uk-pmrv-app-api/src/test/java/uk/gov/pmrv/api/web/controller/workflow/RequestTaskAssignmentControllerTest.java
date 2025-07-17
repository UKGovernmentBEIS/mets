package uk.gov.pmrv.api.web.controller.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.AssigneeUserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.RequestTaskAssignmentDTO;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentQueryService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.UserRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestTaskAssignmentControllerTest {

    private static final String BASE_PATH = "/v1.0/tasks-assignment";
    private static final String CANDIDATE_ASSIGNEES = "/candidate-assignees";
    private static final String ASSIGN = "/assign";

    private static final String USER_ID = "user_id";
    private static final Long TASK_ID = 1L;

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @InjectMocks
    private RequestTaskAssignmentController requestTaskAssignmentController;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private UserRequestTaskAssignmentService userRequestTaskAssignmentService;

    @Mock
    private RequestTaskAssignmentQueryService requestTaskAssignmentQueryService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(requestTaskAssignmentController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        requestTaskAssignmentController = (RequestTaskAssignmentController) aopProxy.getProxy();
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(requestTaskAssignmentController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .build();
    }

    @Test
    void assignTask() throws Exception {
        AppUser appUser = buildAppUser();
        RequestTaskAssignmentDTO
                requestTaskAssignmentDTO = RequestTaskAssignmentDTO.builder().taskId(1L).userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doNothing().when(userRequestTaskAssignmentService).assignTask(requestTaskAssignmentDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + ASSIGN )
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestTaskAssignmentDTO)))
                .andExpect(status().isNoContent());

        verify(userRequestTaskAssignmentService, times(1)).assignTask(requestTaskAssignmentDTO);
    }

    @Test
    void assignTask_forbidden() throws Exception {
        AppUser appUser = buildAppUser();
        RequestTaskAssignmentDTO
                requestTaskAssignmentDTO = RequestTaskAssignmentDTO.builder().taskId(1L).userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "assignTask", "1", null, null);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + ASSIGN )
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestTaskAssignmentDTO)))
                .andExpect(status().isForbidden());

        verify(userRequestTaskAssignmentService, never()).assignTask(any());
    }

    @Test
    void getCandidateAssigneesByTaskId() throws Exception {
        AppUser appUser = buildAppUser();
        String userId1 = "userId1";
        String userId2 = "userId2";
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = buildMockUserInfoList(Arrays.asList(userId1, userId2));

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(TASK_ID, appUser))
            .thenReturn(candidateAssigneesInfo);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + TASK_ID + CANDIDATE_ASSIGNEES)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(candidateAssigneesInfo.size())))
            .andExpect(jsonPath("$[*].id").value(containsInAnyOrder(userId1, userId2)));

        verify(requestTaskAssignmentQueryService, times(1))
                .getCandidateAssigneesByTaskId(TASK_ID, appUser);
    }

    @Test
    void getCandidateAssigneesByTaskId_forbidden() throws Exception {
        AppUser appUser = buildAppUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "getCandidateAssigneesByTaskId", "1", null, null);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + TASK_ID + CANDIDATE_ASSIGNEES)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(requestTaskAssignmentQueryService, never()).getCandidateAssigneesByTaskId(anyLong(), any());
    }

    @Test
    void getCandidateAssigneesByTaskType() throws Exception {
        RequestTaskType taskType = RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW;
        AppUser appUser = buildAppUser();
        String userId1 = "userId1";
        String userId2 = "userId2";
        List<AssigneeUserInfoDTO> candidateAssigneesInfo = buildMockUserInfoList(Arrays.asList(userId1, userId2));

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(requestTaskAssignmentQueryService.getCandidateAssigneesByTaskType(taskType, appUser))
            .thenReturn(candidateAssigneesInfo);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + TASK_ID + CANDIDATE_ASSIGNEES + "/" + taskType.name())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(candidateAssigneesInfo.size())))
            .andExpect(jsonPath("$[*].id").value(containsInAnyOrder(userId1, userId2)));

        verify(requestTaskAssignmentQueryService, times(1))
            .getCandidateAssigneesByTaskType(taskType, appUser);
    }

    @Test
    void getCandidateAssigneesByTaskType_forbidden() throws Exception {
        RequestTaskType taskType = RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW;
        AppUser appUser = buildAppUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "getCandidateAssigneesByTaskType", "1", null, null);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + TASK_ID + CANDIDATE_ASSIGNEES + "/" + taskType.name())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(requestTaskAssignmentQueryService);
    }

    private AppUser buildAppUser() {
        return AppUser.builder().userId(USER_ID).roleType(RoleTypeConstants.REGULATOR).build();
    }

    private List<AssigneeUserInfoDTO> buildMockUserInfoList(List<String> userIds) {
        return userIds.stream()
            .map(userId -> AssigneeUserInfoDTO.builder().id(userId).firstName(userId).lastName(userId).build())
            .collect(Collectors.toList());
    }

}
