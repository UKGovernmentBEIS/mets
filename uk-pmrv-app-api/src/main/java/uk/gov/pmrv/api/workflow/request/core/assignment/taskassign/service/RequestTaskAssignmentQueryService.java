package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.resource.RequestTaskAuthorizationResourceService;
import uk.gov.netz.api.authorization.rules.services.resource.ResourceCriteria;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.AssigneeUserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.transform.AssigneeUserInfoMapper;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for performing query assignments on {@link RequestTask} objects.
 */
@Service
@RequiredArgsConstructor
public class RequestTaskAssignmentQueryService {

    private final RequestTaskService requestTaskService;
    private final UserAuthService userAuthService;
    private final RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;
    private final RequestTaskAuthorizationResourceService requestTaskAuthorizationResourceService;
    private final AssigneeUserInfoMapper assigneeUserInfoMapper = Mappers.getMapper(AssigneeUserInfoMapper.class);

    /**
     * Retrieves all user that have the authority to be assigned to the provided task id.
     * @param taskId the task Id
     * @param authenticatedUser the user {@link AppUser} that performs the action
     * @return {@link List} of {@link AssigneeUserInfoDTO}
     */
    @Transactional(readOnly = true)
    public List<AssigneeUserInfoDTO> getCandidateAssigneesByTaskId(Long taskId, AppUser authenticatedUser) {
        RequestTask requestTask = requestTaskService.findTaskById(taskId);

        requestTaskAssignmentValidationService.validateTaskAssignmentCapability(requestTask);

        ResourceCriteria resourceCriteria =
                ResourceCriteria.builder()
                        .requestResources(requestTask.getRequest().getRequestResourcesMap())
                        .build();

        List<String> candidateAssignees = getCandidateAssigneesByCriteriaAndRoleType(
                requestTask.getType(),
                resourceCriteria,
                authenticatedUser.getRoleType());

        if(isPeerReviewTask(requestTask.getType())) {
            candidateAssignees.remove(requestTask.getRequest().getPayload().getRegulatorReviewer());
        }

        return getCandidateAssigneesUserInfo(candidateAssignees);
    }

    @Transactional(readOnly = true)
    public List<AssigneeUserInfoDTO> getCandidateAssigneesByTaskType(RequestTaskType taskType, AppUser authenticatedUser) {
        requestTaskAssignmentValidationService.validateTaskAssignmentCapability(taskType);

        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.CA, authenticatedUser.getCompetentAuthority().name()))
                .build();

        List<String> candidateAssignees = getCandidateAssigneesByCriteriaAndRoleType(
                taskType,
                resourceCriteria,
                authenticatedUser.getRoleType());

        if(isPeerReviewTask(taskType)) {
            candidateAssignees.remove(authenticatedUser.getUserId());
        }

        return getCandidateAssigneesUserInfo(candidateAssignees);
    }


    private List<String> getCandidateAssigneesByCriteriaAndRoleType(RequestTaskType requestTaskType,
                                                                    ResourceCriteria resourceCriteria, String roleType) {
        return requestTaskAuthorizationResourceService.findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
                requestTaskType.name(),
                resourceCriteria,
                roleType);
    }

    private boolean isPeerReviewTask(RequestTaskType requestTaskType) {
        return requestTaskType.isPeerReview();
    }

    private List<AssigneeUserInfoDTO> getCandidateAssigneesUserInfo(List<String> candidateAssignees) {
        return userAuthService.getUsers(candidateAssignees).stream()
                .map(assigneeUserInfoMapper::toAssigneeUserInfoDTO)
                .sorted(Comparator.comparing(AssigneeUserInfoDTO::getFirstName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(AssigneeUserInfoDTO::getLastName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }
}
