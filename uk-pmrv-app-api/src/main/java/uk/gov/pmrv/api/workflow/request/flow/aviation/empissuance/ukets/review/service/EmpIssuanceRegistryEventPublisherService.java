package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceApprovedEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

@Service
@RequiredArgsConstructor
public class EmpIssuanceRegistryEventPublisherService {

    private final ApplicationEventPublisher publisher;

    public void publishRegistryEvent(EmpIssuanceUkEtsRequestPayload payload,String requestId,Long accountId) {
        if(EmpIssuanceDeterminationType.APPROVED.equals(payload.getDetermination().getType())) {
            publisher.publishEvent(EmpIssuanceApprovedEvent.builder().accountId(accountId).requestId(requestId).build());
        }
    }

}
