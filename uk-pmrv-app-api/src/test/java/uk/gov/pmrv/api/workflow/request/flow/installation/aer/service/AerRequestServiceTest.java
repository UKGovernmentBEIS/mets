package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.rules.domain.PmrvScope.REQUEST_MARK_NOT_REQUIRED;

@ExtendWith(MockitoExtension.class)
class AerRequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private AerRequestService aerRequestService;

    @Mock
    private RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void canMarkAsNotRequired_whenRequestExistsAndIsAerInProgress_thenReturnTrue() {
        String requestId = "1";
        Request request = new Request();
        request.setType(RequestType.AER);
        request.setStatus(RequestStatus.IN_PROGRESS);

        AppUser appUser = AppUser.builder()
                .userId("testUser")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        when(regulatorAuthorityResourceService.findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
                ResourceType.REQUEST, request.getType().name(), REQUEST_MARK_NOT_REQUIRED, appUser.getCompetentAuthority()))
                .thenReturn(List.of(appUser.getUserId()));

        when(workflowService.hasMessageEventSubscriptionWithName(any(), any()))
            .thenReturn(true);

        boolean result = aerRequestService.canMarkAsNotRequired(requestId, appUser);
        assertTrue(result);
    }

    @Test
    void canMarkAsNotRequired_whenRequestExistsAndIsNotAer_thenReturnFalse() {
        String requestId = "2";
        Request request = new Request();
        request.setType(RequestType.PERMIT_ISSUANCE);
        request.setStatus(RequestStatus.IN_PROGRESS);

        AppUser appUser = AppUser.builder()
                .userId("testUser")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        when(workflowService.hasMessageEventSubscriptionWithName(any(), any()))
            .thenReturn(true);

        boolean result = aerRequestService.canMarkAsNotRequired(requestId, appUser);
        assertFalse(result);
    }

    @Test
    void canMarkAsNotRequired_whenRequestExistsAndIsNotInProgress_thenReturnFalse() {
        String requestId = "3";
        Request request = new Request();
        request.setType(RequestType.AER);
        request.setStatus(RequestStatus.COMPLETED);

        AppUser appUser = AppUser.builder()
                .userId("testUser")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        when(workflowService.hasMessageEventSubscriptionWithName(any(), any()))
            .thenReturn(true);

        boolean result = aerRequestService.canMarkAsNotRequired(requestId, appUser);
        assertFalse(result);
    }

    @Test
    void canMarkAsNotRequired_whenRequestDoesNotExist_thenThrowBusinessException() {
        String requestId = "4";

        AppUser appUser = AppUser.builder()
                .userId("testUser")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            aerRequestService.canMarkAsNotRequired(requestId, appUser);
        });

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void canMarkAsNotRequired_whenUserIdNotInPermissionList_thenReturnFalse() {
        String requestId = "5";
        Request request = new Request();
        request.setType(RequestType.AER);
        request.setStatus(RequestStatus.IN_PROGRESS);

        AppUser appUser = AppUser.builder()
                .userId("testUser")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        when(regulatorAuthorityResourceService.findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
                ResourceType.REQUEST, request.getType().name(), REQUEST_MARK_NOT_REQUIRED, appUser.getCompetentAuthority()))
                .thenReturn(List.of("anotherUser"));

        when(workflowService.hasMessageEventSubscriptionWithName(any(), any()))
            .thenReturn(true);

        boolean result = aerRequestService.canMarkAsNotRequired(requestId, appUser);
        assertFalse(result);
    }

    @Test
    void canMarkAsNotRequired_whenOldAer_thenReturnFalse() {
        String requestId = "1";
        Request request = new Request();
        request.setType(RequestType.AER);
        request.setStatus(RequestStatus.IN_PROGRESS);

        AppUser appUser = AppUser.builder()
            .userId("testUser")
            .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
            .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        when(regulatorAuthorityResourceService.findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
            ResourceType.REQUEST, request.getType().name(), REQUEST_MARK_NOT_REQUIRED, appUser.getCompetentAuthority()))
            .thenReturn(List.of(appUser.getUserId()));

        when(workflowService.hasMessageEventSubscriptionWithName(any(), any()))
            .thenReturn(false);

        boolean result = aerRequestService.canMarkAsNotRequired(requestId, appUser);
        assertFalse(result);
    }

}
