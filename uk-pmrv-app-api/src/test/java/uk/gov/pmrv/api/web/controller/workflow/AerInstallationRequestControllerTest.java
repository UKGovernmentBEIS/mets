package uk.gov.pmrv.api.web.controller.workflow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler.AerMarkNotRequiredActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerRequestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AerInstallationRequestControllerTest {

    @Mock
    private AerMarkNotRequiredActionHandler aerMarkNotRequiredActionHandler;

    @Mock
    private AerRequestService requestService;

    @InjectMocks
    private AerInstallationRequestController aerInstallationRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHasAccessMarkAsNotRequired_WhenHasAccess() {
        String requestId = "123";
        AppUser appUser = mock(AppUser.class);
        when(requestService.canMarkAsNotRequired(requestId, appUser)).thenReturn(true);
        ResponseEntity<Boolean> response = aerInstallationRequestController.hasAccessMarkAsNotRequired(appUser, requestId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        verify(requestService, times(1)).canMarkAsNotRequired(requestId, appUser);
    }

    @Test
    void testHasAccessMarkAsNotRequired_WhenNoAccess() {
        String requestId = "123";
        AppUser appUser = mock(AppUser.class);
        when(requestService.canMarkAsNotRequired(requestId, appUser)).thenReturn(false);
        ResponseEntity<Boolean> response = aerInstallationRequestController.hasAccessMarkAsNotRequired(appUser, requestId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody());
        verify(requestService, times(1)).canMarkAsNotRequired(requestId, appUser);
    }
}
