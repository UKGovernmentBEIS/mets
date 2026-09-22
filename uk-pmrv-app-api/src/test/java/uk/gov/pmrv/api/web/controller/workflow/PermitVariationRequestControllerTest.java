package uk.gov.pmrv.api.web.controller.workflow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRequestPaymentDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler.PermitVariationRequestPaymentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationRequestService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PermitVariationRequestControllerTest {

    private static final String REQUEST_ID = "1";

    @Mock
    private PermitVariationRequestPaymentActionHandler permitVariationRequestPaymentActionHandler;

    @Mock
    private PermitVariationRequestService permitVariationRequestService;

    @Mock
    private AppUser appUser;

    @InjectMocks
    private PermitVariationRequestController controller;

    @Test
    void hasAccessRequestPayment_shouldReturnTrue_whenUserHasAccess() {
        when(permitVariationRequestService.canRequestPayment(REQUEST_ID))
                .thenReturn(true);

        ResponseEntity<Boolean> response =
                controller.hasAccessRequestPayment(appUser, REQUEST_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();

        verify(permitVariationRequestService)
                .canRequestPayment(REQUEST_ID);
    }

    @Test
    void hasAccessRequestPayment_shouldReturnFalse_whenUserDoesNotHaveAccess() {
        when(permitVariationRequestService.canRequestPayment(REQUEST_ID))
                .thenReturn(false);

        ResponseEntity<Boolean> response =
                controller.hasAccessRequestPayment(appUser, REQUEST_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isFalse();

        verify(permitVariationRequestService)
                .canRequestPayment(REQUEST_ID);
    }

    @Test
    void requestPayment_shouldProcessRequestAndReturnOk() {
        PermitVariationRequestPaymentDetails paymentDetails =
                PermitVariationRequestPaymentDetails.builder()
                        .amount(BigDecimal.valueOf(150))
                        .dueDate(LocalDate.of(2026, 9, 30))
                        .build();

        ResponseEntity<Void> response =
                controller.requestPayment(
                        appUser,
                        REQUEST_ID,
                        paymentDetails
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(permitVariationRequestPaymentActionHandler)
                .process(
                        REQUEST_ID,
                        appUser,
                        paymentDetails
                );
    }
}
