package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedRegistryEvent;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.InstallationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;

@Service
@RequiredArgsConstructor
public class InstallationAccountRegistryEventPublisherService {

    private final ApplicationEventPublisher publisher;
    private final AccountQueryService accountQueryService;

    public void publishRegistryEvent(PermitIssuanceRequestPayload payload, String requestId, Long accountId) {

        if (PermitType.GHGE.equals(payload.getPermitType())
                && DeterminationType.GRANTED.equals(payload.getDetermination().getType())
                && EmissionTradingScheme.UK_ETS_INSTALLATIONS.equals(accountQueryService.getAccountEmissionTradingScheme(accountId))) {
            publisher.publishEvent(InstallationAccountCreatedRegistryEvent.builder().accountId(accountId).requestId(requestId).build());
        }

    }

    public void publishVariationRegistryEvent(PermitVariationRequestPayload payload, String requestId, Long accountId) {
        PermitContainer originalContainer = payload.getOriginalPermitContainer();

        if (PermitType.GHGE.equals(payload.getPermitType()) && !PermitType.GHGE.equals(originalContainer.getPermitType())
                && EmissionTradingScheme.UK_ETS_INSTALLATIONS.equals(accountQueryService.getAccountEmissionTradingScheme(accountId))) {
            publisher.publishEvent(InstallationAccountCreatedRegistryEvent.builder().accountId(accountId).requestId(requestId).build());
        }

        if(PermitType.GHGE.equals(payload.getPermitType()) && PermitType.GHGE.equals(originalContainer.getPermitType())
                && EmissionTradingScheme.UK_ETS_INSTALLATIONS.equals(accountQueryService.getAccountEmissionTradingScheme(accountId))) {
            publisher.publishEvent(InstallationAccountUpdatedRegistryEvent.builder().accountId(accountId).requestId(requestId).build());
        }
    }

}
