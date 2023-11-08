package uk.gov.pmrv.api.workflow.request.flow.payment.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentTrackRequestTaskPayload;

import java.time.LocalDate;
import java.util.Set;

@Service
public class TrackPaymentInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        return PaymentTrackRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PAYMENT_TRACK_PAYLOAD)
                .amount(request.getPayload().getPaymentAmount())
                .creationDate(LocalDate.now())
                .paymentRefNum(request.getId())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return RequestTaskType.getTrackPaymentTypes();
    }
}
