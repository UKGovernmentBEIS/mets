package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedRegistryEvent;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InstallationAccountCreatedRegistryEventPublisherServiceTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private AccountQueryService accountQueryService;

    @InjectMocks
    private InstallationAccountRegistryEventPublisherService installationAccountRegistryEventPublisherService;

    @Test
    void permitGranted_isPublished() {

        PermitIssuanceRequestPayload payload =
                PermitIssuanceRequestPayload.builder().determination(PermitIssuanceGrantDetermination.builder()
                                .type(DeterminationType.GRANTED).build())
                .permitType(PermitType.GHGE).build();

        when(accountQueryService.getAccountEmissionTradingScheme(1L)).thenReturn(EmissionTradingScheme.UK_ETS_INSTALLATIONS);

        installationAccountRegistryEventPublisherService.publishRegistryEvent(payload,"request-id",1L);

        verify(applicationEventPublisher).publishEvent(InstallationAccountCreatedRegistryEvent
                .builder().accountId(1L).requestId("request-id").build());

    }

    @Test
    void permitGranted_isNotPublishedBecauseOfTradingScheme() {

        PermitIssuanceRequestPayload payload =
                PermitIssuanceRequestPayload.builder().determination(PermitIssuanceGrantDetermination.builder()
                                .type(DeterminationType.GRANTED).build())
                        .permitType(PermitType.GHGE).build();

        when(accountQueryService.getAccountEmissionTradingScheme(1L)).thenReturn(EmissionTradingScheme.EU_ETS_INSTALLATIONS);

        installationAccountRegistryEventPublisherService.publishRegistryEvent(payload,"request-id",1L);

        verifyNoInteractions(applicationEventPublisher);

    }

    @Test
    void permitGranted_isNotPublishedBecauseOfPermitType() {

        PermitIssuanceRequestPayload payload =
                PermitIssuanceRequestPayload.builder().determination(PermitIssuanceGrantDetermination.builder()
                                .type(DeterminationType.GRANTED).build())
                        .permitType(PermitType.HSE).build();


        installationAccountRegistryEventPublisherService.publishRegistryEvent(payload,"request-id",1L);

        verifyNoInteractions(applicationEventPublisher);

    }

    @Test
    void permitGranted_isNotPublishedBecauseOfPermitRejection() {

        PermitIssuanceRequestPayload payload =
                PermitIssuanceRequestPayload.builder().determination(PermitIssuanceRejectDetermination.builder()
                                .type(DeterminationType.REJECTED).build())
                        .permitType(PermitType.GHGE).build();


        installationAccountRegistryEventPublisherService.publishRegistryEvent(payload,"request-id",1L);

        verifyNoInteractions(applicationEventPublisher);

    }

    @Test
    void permitVariation_isPublished() {

        PermitVariationRequestPayload payload =
                PermitVariationRequestPayload.builder().originalPermitContainer(PermitContainer.builder().permitType(PermitType.HSE).build())
                        .permitType(PermitType.GHGE).build();

        when(accountQueryService.getAccountEmissionTradingScheme(1L)).thenReturn(EmissionTradingScheme.UK_ETS_INSTALLATIONS);

        installationAccountRegistryEventPublisherService.publishVariationRegistryEvent(payload,"request-id",1L);

        verify(applicationEventPublisher).publishEvent(InstallationAccountCreatedRegistryEvent
                .builder().accountId(1L).requestId("request-id").build());

    }

    @Test
    void permitVariation_isNotPublished() {

        PermitVariationRequestPayload payload =
                PermitVariationRequestPayload.builder()
                        .permitType(PermitType.HSE).build();

        installationAccountRegistryEventPublisherService.publishVariationRegistryEvent(payload,"request-id",1L);

        verifyNoInteractions(applicationEventPublisher);

    }

    @Test
    void permitVariation_isNotPublished_TradingScheme() {

        PermitVariationRequestPayload payload =
                PermitVariationRequestPayload.builder().originalPermitContainer(PermitContainer.builder().permitType(PermitType.HSE).build())
                        .permitType(PermitType.GHGE).build();

        when(accountQueryService.getAccountEmissionTradingScheme(1L)).thenReturn(EmissionTradingScheme.EU_ETS_INSTALLATIONS);

        installationAccountRegistryEventPublisherService.publishVariationRegistryEvent(payload,"request-id",1L);

        verifyNoInteractions(applicationEventPublisher);

    }

}
