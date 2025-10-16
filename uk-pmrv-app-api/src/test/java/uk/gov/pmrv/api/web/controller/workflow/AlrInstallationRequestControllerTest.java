package uk.gov.pmrv.api.web.controller.workflow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AlrRequestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AlrInstallationRequestControllerTest {

    @Mock
    private AlrRequestService requestService;

    @InjectMocks
    private AlrInstallationRequestController alrInstallationRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHasAccessMarkAsNotRequired_WhenHasAccess() {
        String requestId = "123";
        AppUser appUser = mock(AppUser.class);
        when(requestService.userCanMarkAlrAsNotRequired(requestId, appUser)).thenReturn(true);
        ResponseEntity<Boolean> response = alrInstallationRequestController.hasAccessMarkAsNotRequiredAlr(appUser, requestId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        verify(requestService, times(1)).userCanMarkAlrAsNotRequired(requestId, appUser);
    }

    @Test
    void testHasAccessMarkAsNotRequired_WhenNoAccess() {
        String requestId = "123";
        AppUser appUser = mock(AppUser.class);
        when(requestService.userCanMarkAlrAsNotRequired(requestId, appUser)).thenReturn(false);
        ResponseEntity<Boolean> response = alrInstallationRequestController.hasAccessMarkAsNotRequiredAlr(appUser, requestId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody());
        verify(requestService, times(1)).userCanMarkAlrAsNotRequired(requestId, appUser);
    }

}
