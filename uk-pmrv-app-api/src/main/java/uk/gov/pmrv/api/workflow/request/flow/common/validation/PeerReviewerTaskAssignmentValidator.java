package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.resource.RequestTaskAuthorizationResourceService;
import uk.gov.netz.api.authorization.rules.services.resource.ResourceCriteria;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PeerReviewerTaskAssignmentValidator {

    private final RequestTaskAuthorizationResourceService requestTaskAuthorizationResourceService;

    public void validate(RequestTaskType requestTaskTypeToBeAssigned, String selectedAssignee, AppUser appUser) {
        if (!hasUserPermissionsToBeAssignedToTaskType(requestTaskTypeToBeAssigned, selectedAssignee, appUser)) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED);
        }
    }

    public boolean hasUserPermissionsToBeAssignedToTaskType(RequestTaskType requestTaskType, String userId, AppUser appUser) {
        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
                .requestResources(Map.of(
                        ResourceType.CA, appUser.getCompetentAuthority().name()))
                .build();

        List<String> candidateAssignees = requestTaskAuthorizationResourceService
                .findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
                        requestTaskType.name(),
                        resourceCriteria,
                        appUser.getRoleType());

        candidateAssignees = new ArrayList<>(candidateAssignees);
        if(requestTaskType.isPeerReview()) {
            candidateAssignees.remove(appUser.getUserId());
        }

        return candidateAssignees.contains(userId);
    }

}
