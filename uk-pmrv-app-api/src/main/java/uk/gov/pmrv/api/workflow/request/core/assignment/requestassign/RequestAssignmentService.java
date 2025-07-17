package uk.gov.pmrv.api.workflow.request.core.assignment.requestassign;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;

@Log4j2
@Service
@RequiredArgsConstructor
public class RequestAssignmentService {

    private final UserRoleTypeService userRoleTypeService;
    
    /**
     * Set the provided user as assignee to the provided {@link Request}
     * @param request the {@link Request}
     * @param userId the user id
     */
    @Transactional
    public void assignRequestToUser(Request request, String userId) {
        if (ObjectUtils.isEmpty(userId)) {
            log.error("A non empty user should be assigned request '{}'",request::getId);
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED);
        }

        UserRoleTypeDTO userRoleType = userRoleTypeService.getUserRoleTypeByUserId(userId);

        switch (userRoleType.getRoleType()) {
            case RoleTypeConstants.OPERATOR:
                assignRequestToOperatorUser(request, userId);
                break;
            case RoleTypeConstants.REGULATOR:
                assignRequestToRegulatorUser(request, userId);
                break;
            case RoleTypeConstants.VERIFIER:
                assignRequestToVerifierUser(request, userId);
                break;
            default:
                throw new UnsupportedOperationException(String.format("User with role type %s not related with request assignment", userRoleType.getRoleType()));
        }
    }

    private void assignRequestToOperatorUser(Request request, String userId) {
        RequestPayload requestPayload = request.getPayload();
        if (!userId.equals(requestPayload.getOperatorAssignee())) {
            requestPayload.setOperatorAssignee(userId);
        }
    }

    private void assignRequestToRegulatorUser(Request request, String userId) {
        RequestPayload requestPayload = request.getPayload();
        if (!userId.equals(requestPayload.getRegulatorAssignee())) {
            requestPayload.setRegulatorAssignee(userId);
        }
    }

    private void assignRequestToVerifierUser(Request request, String userId) {
        RequestPayload requestPayload = request.getPayload();
        if (!userId.equals(requestPayload.getVerifierAssignee())) {
            requestPayload.setVerifierAssignee(userId);
        }
    }
}
