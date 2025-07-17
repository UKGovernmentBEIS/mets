package uk.gov.pmrv.api.workflow.request.flow.common.validation;

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
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeerReviewerTaskAssignmentValidatorTest {

    @Mock
    private RequestTaskAuthorizationResourceService requestTaskAuthorizationResourceService;

    @InjectMocks
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Test
    void validate() {
        AppUser appUser = AppUser.builder().userId("userId")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .roleType(RoleTypeConstants.REGULATOR).build();
        String peerReviewer = "peerReviewer";
        RequestTaskType requestTaskType = RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW;

        when(requestTaskAuthorizationResourceService.
                findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType.name(),
                ResourceCriteria.builder()
                        .requestResources(Map.of(
                                ResourceType.CA, appUser.getCompetentAuthority().name()))
                        .build(),
                appUser.getRoleType()))
                .thenReturn(Arrays.asList("userId", "peerReviewer"));

        peerReviewerTaskAssignmentValidator.validate(requestTaskType, peerReviewer, appUser);
    }

    @Test
    void validate_assignment_not_allowed() {
        AppUser appUser = AppUser.builder().userId("userId")
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();
        String peerReviewer = "peerReviewer";
        RequestTaskType requestTaskType = RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW;

        when(requestTaskAuthorizationResourceService.
                findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(requestTaskType.name(),
                ResourceCriteria.builder()
                        .requestResources(Map.of(
                                ResourceType.CA, appUser.getCompetentAuthority().name()))
                        .build(),
                appUser.getRoleType()))
                .thenReturn(List.of("userId"));

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> peerReviewerTaskAssignmentValidator.validate(requestTaskType, peerReviewer, appUser));

        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, businessException.getErrorCode());
    }
}