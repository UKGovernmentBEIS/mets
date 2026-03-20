package uk.gov.pmrv.api.web.controller.workflow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesReopenAvailabilityService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdFlagRequestControllerTest {

    @InjectMocks
    private WithholdFlagRequestController controller;

    @Mock
    private WithholdingOfAllowancesReopenAvailabilityService availabilityService;

    @Test
    void isWithholdFlagReopenAvailable_true() {
        Long accountId = 1L;
        when(availabilityService.isWithholdReopenAvailable(accountId)).thenReturn(true);

        ResponseEntity<Boolean> response = controller.isWithholdFlagReopenAvailable(accountId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
        verify(availabilityService, times(1)).isWithholdReopenAvailable(accountId);
    }

    @Test
    void isWithholdFlagReopenAvailable_false() {
        Long accountId = 1L;
        when(availabilityService.isWithholdReopenAvailable(accountId)).thenReturn(false);

        ResponseEntity<Boolean> response = controller.isWithholdFlagReopenAvailable(accountId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isFalse();
        verify(availabilityService, times(1)).isWithholdReopenAvailable(accountId);
    }
}
