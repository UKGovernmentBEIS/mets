package uk.gov.pmrv.api.workflow.request.core.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

import java.time.LocalDateTime;

@Validated
@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    /**
     * Returns request by request id.
     *
     * @param id Request id
     * @return {@link Request}
     */
    public Request findRequestById(String id) {
        return requestRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }


    @Transactional
    public void addActionToRequest(Request request, @Valid RequestActionPayload payload,
                                   RequestActionType actionType, String submittedBy) {

        final String fullName = submittedBy != null ? requestActionUserInfoResolver.getUserFullName(submittedBy) : null;

        request.addRequestAction(
                RequestAction.builder()
                        .payload(payload)
                        .type(actionType)
                        .submitterId(submittedBy)
                        .submitter(fullName)
                        .build());
    }

    @Transactional
    public void updateRequestStatus(String requestId, RequestStatus status) {
        Request request = findRequestById(requestId);

        request.setStatus(status);
    }

    @Transactional
    public void terminateRequest(String requestId, String processInstanceId, boolean shouldBeDeleted) {
        Request request = findRequestById(requestId);

        if(processInstanceId.equals(request.getProcessInstanceId())){
            if (shouldBeDeleted || 
            		RequestType.SYSTEM_MESSAGE_NOTIFICATION.equals(request.getType())) {
                requestRepository.delete(request);
            } else {
                closeRequest(request);
            }
        }
    }

    @Transactional
    public void paymentCompleted(final String requestId) {
        this.findRequestById(requestId).getPayload().setPaymentCompleted(true);
    }

    private void closeRequest(Request request) {
        if(RequestStatus.IN_PROGRESS.equals(request.getStatus())){
            request.setStatus(RequestStatus.COMPLETED);
        }

        if(!request.getType().isHoldHistory()) {
            request.setPayload(null);
        }

        request.setEndDate(LocalDateTime.now());
    }
}
