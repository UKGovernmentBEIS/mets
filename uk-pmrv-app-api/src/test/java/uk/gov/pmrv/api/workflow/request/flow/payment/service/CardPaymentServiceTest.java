package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.client.service.GovukPayService;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentCreateInfo;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentCreateResult;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetInfo;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetResult;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentStateInfo;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.CardPaymentCreateResponseDTO;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.CardPaymentProcessResponseDTO;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.CardPaymentStateDTO;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMakeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentOutcome;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardPaymentServiceTest {

    @InjectMocks
    private CardPaymentService cardPaymentService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private GovukPayService govukPayService;

    @Mock
    private PaymentCompleteService paymentCompleteService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private WebAppProperties webAppProperties;

    @Test
    void processPayment_create_new() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        AppUser authUser = AppUser.builder().build();
        String paymentRefNum = "AEM-098-1";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .build();
        String webUrl = "http://www.pmrv.uk";
        WebAppProperties web = createAppWeb(webUrl);
        String cardPaymentDesc = "Pay for a permit";
        PaymentCreateInfo paymentCreateInfo = PaymentCreateInfo.builder()
            .competentAuthority(competentAuthority)
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .description(cardPaymentDesc)
            .returnUrl(webUrl + "/payment/" + requestTaskId + "/make/confirmation?method=CREDIT_OR_DEBIT_CARD")
            .build();
        String clientPaymentId = "clientPaymentId";
        String nextUrl = "nextUrl";
        PaymentCreateResult paymentCreateResult = PaymentCreateResult.builder()
            .paymentId(clientPaymentId)
            .nextUrl(nextUrl)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(webAppProperties.getUrl()).thenReturn(webUrl);
        when(govukPayService.createPayment(paymentCreateInfo)).thenReturn(paymentCreateResult);

        //invoke
        CardPaymentCreateResponseDTO cardPaymentCreateResponseDTO =
            cardPaymentService.createCardPayment(requestTaskId, requestTaskActionType, authUser);

        //verify
        assertThat(requestTask.getPayload()).isInstanceOf(PaymentMakeRequestTaskPayload.class);
        PaymentMakeRequestTaskPayload taskPayloadSaved = (PaymentMakeRequestTaskPayload) requestTask.getPayload();
        assertEquals(clientPaymentId, taskPayloadSaved.getExternalPaymentId());

        assertNotNull(cardPaymentCreateResponseDTO);
        assertEquals(nextUrl, cardPaymentCreateResponseDTO.getNextUrl());
        assertNull(cardPaymentCreateResponseDTO.getPendingPaymentExist());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(govukPayService, times(1)).createPayment(paymentCreateInfo);
    }

    @Test
    void processPayment_pending_payment_exists() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        AppUser authUser = AppUser.builder().build();
        String paymentRefNum = "AEM-098-1";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        String externalPaymentId = "externalPaymentId";
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .externalPaymentId(externalPaymentId)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        CardPaymentCreateResponseDTO cardPaymentCreateResponseDTO =
            cardPaymentService.createCardPayment(requestTaskId, requestTaskActionType, authUser);

        //verify
        assertNotNull(cardPaymentCreateResponseDTO);
        assertTrue(cardPaymentCreateResponseDTO.getPendingPaymentExist());
        assertNull(cardPaymentCreateResponseDTO.getNextUrl());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verifyNoInteractions(govukPayService);
        verifyNoMoreInteractions(requestTaskService);
    }

    @Test
    void processPayment_not_supported() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        AppUser authUser = AppUser.builder().build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .paymentMethodTypes(Set.of(PaymentMethodType.BANK_TRANSFER))
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> cardPaymentService.createCardPayment(requestTaskId, requestTaskActionType, authUser));

        //verify
        assertEquals(ErrorCode.INVALID_PAYMENT_METHOD, businessException.getErrorCode());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verifyNoMoreInteractions(requestTaskService);
        verifyNoInteractions(govukPayService);
    }

    @Test
    void getCardPaymentState_finished_with_status_success() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        String userId = "userId";
        AppUser authUser = AppUser.builder().userId(userId).build();
        String paymentRefNum = "AEM-098-1";
        String externalPaymentId = "n4brhul26f2hn1lt992ejj10ht";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .externalPaymentId(externalPaymentId)
            .build();
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId(processTaskId)
            .build();
        PaymentGetInfo paymentGetInfo = PaymentGetInfo.builder()
            .paymentId(externalPaymentId)
            .competentAuthority(competentAuthority)
            .build();
        PaymentStateInfo paymentStateInfo = PaymentStateInfo.builder()
            .status("success")
            .finished(true)
            .build();
        PaymentGetResult paymentGetResult = PaymentGetResult.builder()
            .paymentId(externalPaymentId)
            .state(paymentStateInfo)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(govukPayService.getPayment(paymentGetInfo)).thenReturn(paymentGetResult);

        //invoke
        CardPaymentProcessResponseDTO cardPaymentStateResponse =
            cardPaymentService.processExistingCardPayment(requestTaskId, requestTaskActionType, authUser);

        assertEquals(externalPaymentId, cardPaymentStateResponse.getPaymentId());
        assertNull(cardPaymentStateResponse.getNextUrl());
        CardPaymentStateDTO cardPaymentStateDTO = cardPaymentStateResponse.getState();
        assertNotNull(cardPaymentStateDTO);
        assertEquals(paymentStateInfo.getStatus(), cardPaymentStateDTO.getStatus());
        assertEquals(paymentStateInfo.isFinished(), cardPaymentStateDTO.isFinished());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(govukPayService, times(1)).getPayment(paymentGetInfo);
        verify(paymentCompleteService, times(1)).complete(requestTask, authUser);
        verify(workflowService, times(1))
            .completeTask(processTaskId, Map.of(BpmnProcessConstants.PAYMENT_OUTCOME, PaymentOutcome.SUCCEEDED));
    }

    @Test
    void getCardPaymentState_no_payment_id() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        AppUser authUser = AppUser.builder().build();
        String paymentRefNum = "AEM-098-1";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .build();
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId(processTaskId)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> cardPaymentService.processExistingCardPayment(requestTaskId, requestTaskActionType, authUser));

        //verify
        assertEquals(ErrorCode.EXTERNAL_PAYMENT_ID_NOT_EXIST, businessException.getErrorCode());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verifyNoInteractions(govukPayService, paymentCompleteService, workflowService);
    }

    @Test
    void getCardPaymentState_finished_with_status_not_success() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        String userId = "userId";
        AppUser authUser = AppUser.builder().userId(userId).build();
        String paymentRefNum = "AEM-098-1";
        String externalPaymentId = "n4brhul26f2hn1lt992ejj10ht";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .externalPaymentId(externalPaymentId)
            .build();
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId(processTaskId)
            .build();
        PaymentGetInfo paymentGetInfo = PaymentGetInfo.builder()
            .paymentId(externalPaymentId)
            .competentAuthority(competentAuthority)
            .build();
        PaymentStateInfo paymentStateInfo = PaymentStateInfo.builder()
            .status("fail")
            .finished(true)
            .code("P0020")
            .message("Payment expired")
            .build();
        PaymentGetResult paymentGetResult = PaymentGetResult.builder()
            .paymentId(externalPaymentId)
            .state(paymentStateInfo)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(govukPayService.getPayment(paymentGetInfo)).thenReturn(paymentGetResult);

        //invoke
        CardPaymentProcessResponseDTO cardPaymentStateResponse =
            cardPaymentService.processExistingCardPayment(requestTaskId, requestTaskActionType, authUser);


        //verify
        assertEquals(externalPaymentId, cardPaymentStateResponse.getPaymentId());
        assertNull(cardPaymentStateResponse.getNextUrl());
        CardPaymentStateDTO cardPaymentStateDTO = cardPaymentStateResponse.getState();
        assertNotNull(cardPaymentStateDTO);
        assertEquals(paymentStateInfo.getStatus(), cardPaymentStateDTO.getStatus());
        assertEquals(paymentStateInfo.isFinished(), cardPaymentStateDTO.isFinished());
        assertEquals(paymentStateInfo.getCode(), cardPaymentStateDTO.getCode());
        assertEquals(paymentStateInfo.getMessage(), cardPaymentStateDTO.getMessage());

        assertThat(requestTask.getPayload()).isInstanceOf(PaymentMakeRequestTaskPayload.class);
        PaymentMakeRequestTaskPayload taskPayloadSaved = (PaymentMakeRequestTaskPayload) requestTask.getPayload();
        assertNull(taskPayloadSaved.getExternalPaymentId());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(govukPayService, times(1)).getPayment(paymentGetInfo);
        verifyNoInteractions(paymentCompleteService, workflowService);
    }

    @Test
    void getCardPaymentState_not_finished() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        String userId = "userId";
        AppUser authUser = AppUser.builder().userId(userId).build();
        String paymentRefNum = "AEM-098-1";
        String externalPaymentId = "n4brhul26f2hn1lt992ejj10ht";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .externalPaymentId(externalPaymentId)
            .build();
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId(processTaskId)
            .build();
        PaymentGetInfo paymentGetInfo = PaymentGetInfo.builder()
            .paymentId(externalPaymentId)
            .competentAuthority(competentAuthority)
            .build();
        PaymentStateInfo paymentStateInfo = PaymentStateInfo.builder()
            .status("created")
            .finished(false)
            .build();
        String nextUrl = "nextUrl";
        PaymentGetResult paymentGetResult = PaymentGetResult.builder()
            .paymentId(externalPaymentId)
            .state(paymentStateInfo)
            .nextUrl(nextUrl)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(govukPayService.getPayment(paymentGetInfo)).thenReturn(paymentGetResult);

        //invoke
        CardPaymentProcessResponseDTO cardPaymentStateResponse =
            cardPaymentService.processExistingCardPayment(requestTaskId, requestTaskActionType, authUser);


        //verify
        assertEquals(externalPaymentId, cardPaymentStateResponse.getPaymentId());
        assertEquals(nextUrl, cardPaymentStateResponse.getNextUrl());
        CardPaymentStateDTO cardPaymentStateDTO = cardPaymentStateResponse.getState();
        assertNotNull(cardPaymentStateDTO);
        assertEquals(paymentStateInfo.getStatus(), cardPaymentStateDTO.getStatus());
        assertEquals(paymentStateInfo.isFinished(), cardPaymentStateDTO.isFinished());
        assertEquals(paymentStateInfo.getCode(), cardPaymentStateDTO.getCode());
        assertEquals(paymentStateInfo.getMessage(), cardPaymentStateDTO.getMessage());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(govukPayService, times(1)).getPayment(paymentGetInfo);
        verifyNoInteractions(paymentCompleteService, workflowService);
    }

    private WebAppProperties createAppWeb(String url) {
        WebAppProperties web = new WebAppProperties();
        web.setUrl(url);
        return web;
    }
}