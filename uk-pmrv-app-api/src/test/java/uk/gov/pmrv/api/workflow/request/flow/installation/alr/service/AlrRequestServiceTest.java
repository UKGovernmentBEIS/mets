package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AlrRequestService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.rules.domain.PmrvScope.REQUEST_MARK_NOT_REQUIRED;

@ExtendWith(MockitoExtension.class)
public class AlrRequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    @InjectMocks
    private AlrRequestService alrRequestService;

    @Test
    void canMarkAsNotRequired_whenRequestExistsAndIsAlrInProgress_thenReturnTrue() {
        String requestId = "1";
        Request request = new Request();
        request.setType(RequestType.ALR);
        request.setStatus(RequestStatus.IN_PROGRESS);

        AppUser appUser = AppUser.builder()
                .userId("testUser")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        when(regulatorAuthorityResourceService.findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
                ResourceType.REQUEST, request.getType().name(), REQUEST_MARK_NOT_REQUIRED, appUser.getCompetentAuthority()))
                .thenReturn(List.of(appUser.getUserId()));

        boolean result = alrRequestService.userCanMarkAlrAsNotRequired(requestId, appUser);
        assertTrue(result);
    }

    @Test
    void canMarkAsNotRequired_whenRequestExistsAndIsAlrNotInProgress_thenReturnFalse() {
        String requestId = "1";
        Request request = new Request();
        request.setType(RequestType.ALR);
        request.setStatus(RequestStatus.COMPLETED);

        AppUser appUser = AppUser.builder()
                .userId("testUser")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        when(regulatorAuthorityResourceService.findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
                ResourceType.REQUEST, request.getType().name(), REQUEST_MARK_NOT_REQUIRED, appUser.getCompetentAuthority()))
                .thenReturn(List.of(appUser.getUserId()));


        boolean result = alrRequestService.userCanMarkAlrAsNotRequired(requestId, appUser);
        assertFalse(result);
    }

    @Test
    void canMarkAsNotRequired_whenRequestExistsAndIsNotAlr_thenReturnFalse() {
        String requestId = "1";
        Request request = new Request();
        request.setType(RequestType.PERMIT_ISSUANCE);
        request.setStatus(RequestStatus.IN_PROGRESS);

        AppUser appUser = AppUser.builder()
                .userId("testUser")
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));


        boolean result = alrRequestService.userCanMarkAlrAsNotRequired(requestId, appUser);
        assertFalse(result);
    }

}
