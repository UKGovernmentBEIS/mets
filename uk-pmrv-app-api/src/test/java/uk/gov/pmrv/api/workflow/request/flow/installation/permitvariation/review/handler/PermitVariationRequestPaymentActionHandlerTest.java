package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRequestPaymentActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRequestPaymentDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationRequestService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PermitVariationRequestPaymentActionHandlerTest {

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private PermitVariationRequestService permitVariationRequestService;

    @Mock
    private AppUser appUser;

    private PermitVariationRequestPaymentActionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PermitVariationRequestPaymentActionHandler(
                requestService,
                workflowService,
                permitVariationRequestService
        );
    }

    @Test
    void process_shouldSavePaymentDetailsAddActionAndSendEvent() {
        String requestId = "1";
        String userId = "userId";
        LocalDate dueDate = LocalDate.of(2026, 9, 30);

        PermitVariationRequestPaymentDetails paymentDetails =
                PermitVariationRequestPaymentDetails.builder()
                        .amount(BigDecimal.valueOf(150))
                        .dueDate(dueDate)
                        .build();

        PermitVariationRequestPayload requestPayload =
                PermitVariationRequestPayload.builder()
                        .build();

        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId))
                .thenReturn(request);

        when(permitVariationRequestService.canRequestPayment(requestId))
                .thenReturn(true);

        when(appUser.getUserId())
                .thenReturn(userId);

        handler.process(requestId, appUser, paymentDetails);

        assertThat(requestPayload.getRequestPaymentDetails())
                .isEqualTo(paymentDetails);

        assertThat(paymentDetails.getManualPaymentInitiationPending())
                .isTrue();

        ArgumentCaptor<PermitVariationRequestPaymentActionPayload> actionPayloadCaptor =
                ArgumentCaptor.forClass(
                        PermitVariationRequestPaymentActionPayload.class
                );

        verify(requestService).addActionToRequest(
                eq(request),
                actionPayloadCaptor.capture(),
                eq(RequestActionType.PERMIT_VARIATION_REQUEST_PAYMENT),
                eq(userId)
        );

        PermitVariationRequestPaymentActionPayload actionPayload =
                actionPayloadCaptor.getValue();

        assertThat(actionPayload.getPaymentDetails())
                .isEqualTo(paymentDetails);

        assertThat(actionPayload.getPayloadType())
                .isEqualTo(
                        RequestActionPayloadType.PERMIT_VARIATION_REQUEST_PAYMENT_PAYLOAD
                );

        Date expectedExpirationDate = DateUtils.atEndOfDay(dueDate);

        verify(workflowService).sendEvent(
                eq(requestId),
                eq(BpmnProcessConstants.PERMIT_VARIATION_INITIATE_PAYMENT),
                eq(Map.of(
                        BpmnProcessConstants.PAYMENT_EXPIRES,
                        true,
                        BpmnProcessConstants.PAYMENT_EXPIRATION_DATE,
                        expectedExpirationDate
                ))
        );
    }

    @Test
    void process_shouldThrowExceptionWhenPaymentCannotBeRequested() {
        String requestId = "1";

        PermitVariationRequestPaymentDetails paymentDetails =
                PermitVariationRequestPaymentDetails.builder()
                        .amount(BigDecimal.valueOf(150))
                        .dueDate(LocalDate.of(2026, 9, 30))
                        .build();

        Request request = Request.builder()
                .id(requestId)
                .payload(PermitVariationRequestPayload.builder().build())
                .build();

        when(requestService.findRequestById(requestId))
                .thenReturn(request);
        when(permitVariationRequestService.canRequestPayment(requestId))
                .thenReturn(false);

        assertThatThrownBy(() ->
                handler.process(requestId, appUser, paymentDetails))
                .isInstanceOf(BusinessException.class);

        verify(requestService, never()).addActionToRequest(
                eq(request),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );

        verify(workflowService, never()).sendEvent(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }
}
