package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.event.PermitGrantedEvent;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

@Service
@RequiredArgsConstructor
public class PermitIssuanceRegistryEventPublisherService {

    private final ApplicationEventPublisher publisher;
    private final AccountQueryService accountQueryService;

    public void publishRegistryEvent(PermitIssuanceRequestPayload payload, String requestId, Long accountId) {

        if (PermitType.GHGE.equals(payload.getPermitType())
                && DeterminationType.GRANTED.equals(payload.getDetermination().getType())
                && EmissionTradingScheme.UK_ETS_INSTALLATIONS.equals(accountQueryService.getAccountEmissionTradingScheme(accountId))) {
            publisher.publishEvent(PermitGrantedEvent.builder().accountId(accountId).requestId(requestId).build());
        }

    }

}
