package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMakeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentStatus;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PaymentCompleteService {

    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Transactional
    public void markAsPaid(RequestTask requestTask, PmrvUser authUser) {
        PaymentMakeRequestTaskPayload requestTaskPayload = (PaymentMakeRequestTaskPayload) requestTask.getPayload();
        validatePaymentMethod(requestTaskPayload);

        String authUserId = authUser.getUserId();
        Request request = requestTask.getRequest();
        RequestPayloadPayable requestPayloadPayable = (RequestPayloadPayable) request.getPayload();
        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
                .paymentCreationDate(requestTaskPayload.getCreationDate())
                .paymentDate(LocalDate.now())
                .paidById(authUserId)
                .paidByFullName(requestActionUserInfoResolver.getUserFullName(authUserId))
                .amount(request.getPayload().getPaymentAmount())
                .status(PaymentStatus.MARK_AS_PAID)
                .paymentMethod(PaymentMethodType.BANK_TRANSFER)
                .paymentRefNum(requestTaskPayload.getPaymentRefNum())
                .build();
        requestPayloadPayable.setRequestPaymentInfo(requestPaymentInfo);

    }

    @Transactional
    public void markAsReceived(RequestTask requestTask, LocalDate receivedDate) {
        RequestPayload requestPayload = requestTask.getRequest().getPayload();
        RequestPayloadPayable requestPayloadPayable = (RequestPayloadPayable) requestPayload;
        PaymentRequestTaskPayload requestTaskPayload = (PaymentRequestTaskPayload) requestTask.getPayload();

        String operatorAssigneeId = requestPayload.getOperatorAssignee();
        RequestPaymentInfo requestPaymentInfo = requestPayloadPayable.getRequestPaymentInfo();
        if(requestPaymentInfo == null) {
            requestPaymentInfo = RequestPaymentInfo.builder()
                    .paymentCreationDate(requestTaskPayload.getCreationDate())
                    .paymentDate(receivedDate)
                    .paidById(requestPayload.getOperatorAssignee())
                    .paidByFullName(requestActionUserInfoResolver.getUserFullName(operatorAssigneeId))
                    .amount(requestPayload.getPaymentAmount())
                    .paymentMethod(PaymentMethodType.BANK_TRANSFER)
                    .paymentRefNum(requestTaskPayload.getPaymentRefNum())
                    .build();
            requestPayloadPayable.setRequestPaymentInfo(requestPaymentInfo);
        }

        requestPaymentInfo.setReceivedDate(receivedDate);
        requestPaymentInfo.setStatus(PaymentStatus.MARK_AS_RECEIVED);
    }

    @Transactional
    public void cancel(RequestTask requestTask, String cancellationReason) {
        RequestPayloadPayable requestPayloadPayable = (RequestPayloadPayable) requestTask.getRequest().getPayload();
        PaymentRequestTaskPayload taskPayload = (PaymentRequestTaskPayload) requestTask.getPayload();
        RequestPaymentInfo requestPaymentInfo = requestPayloadPayable.getRequestPaymentInfo();
        if(requestPaymentInfo == null) {
            requestPaymentInfo = new RequestPaymentInfo();
            requestPayloadPayable.setRequestPaymentInfo(requestPaymentInfo);
        }
        requestPaymentInfo.setPaymentRefNum(taskPayload.getPaymentRefNum());
        requestPaymentInfo.setAmount(taskPayload.getAmount());
        requestPaymentInfo.setPaymentCreationDate(taskPayload.getCreationDate());
        requestPaymentInfo.setCancellationReason(cancellationReason);
        requestPaymentInfo.setStatus(PaymentStatus.CANCELLED);
    }

    @Transactional
    public void complete(RequestTask requestTask, PmrvUser authUser) {
        String authUserId = authUser.getUserId();
        PaymentMakeRequestTaskPayload requestTaskPayload = (PaymentMakeRequestTaskPayload) requestTask.getPayload();
        RequestPayload requestPayload = requestTask.getRequest().getPayload();

        RequestPaymentInfo requestPaymentInfo = RequestPaymentInfo.builder()
                .paymentCreationDate(requestTaskPayload.getCreationDate())
                .paymentDate(LocalDate.now())
                .paidById(authUserId)
                .paidByFullName(requestActionUserInfoResolver.getUserFullName(authUserId))
                .amount(requestPayload.getPaymentAmount())
                .status(PaymentStatus.COMPLETED)
                .paymentMethod(PaymentMethodType.CREDIT_OR_DEBIT_CARD)
                .paymentRefNum(requestTaskPayload.getPaymentRefNum())
                .build();
        ((RequestPayloadPayable) requestPayload).setRequestPaymentInfo(requestPaymentInfo);
    }

    private void validatePaymentMethod(PaymentMakeRequestTaskPayload requestTaskPayload) {
        if(!requestTaskPayload.getPaymentMethodTypes().contains(PaymentMethodType.BANK_TRANSFER)) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_METHOD);
        }
    }
}
