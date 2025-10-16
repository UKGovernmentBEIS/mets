package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.event.PermitGrantedEvent;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PermitIssuanceRegistryEventPublisherServiceTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private AccountQueryService accountQueryService;

    @InjectMocks
    private PermitIssuanceRegistryEventPublisherService permitIssuanceRegistryEventPublisherService;

    @Test
    void permitGranted_isPublished() {

        PermitIssuanceRequestPayload payload =
                PermitIssuanceRequestPayload.builder().determination(PermitIssuanceGrantDetermination.builder()
                                .type(DeterminationType.GRANTED).build())
                .permitType(PermitType.GHGE).build();

        when(accountQueryService.getAccountEmissionTradingScheme(1L)).thenReturn(EmissionTradingScheme.UK_ETS_INSTALLATIONS);

        permitIssuanceRegistryEventPublisherService.publishRegistryEvent(payload,"request-id",1L);

        verify(applicationEventPublisher).publishEvent(PermitGrantedEvent.builder().accountId(1L).requestId("request-id").build());

    }

    @Test
    void permitGranted_isNotPublishedBecauseOfTradingScheme() {

        PermitIssuanceRequestPayload payload =
                PermitIssuanceRequestPayload.builder().determination(PermitIssuanceGrantDetermination.builder()
                                .type(DeterminationType.GRANTED).build())
                        .permitType(PermitType.GHGE).build();

        when(accountQueryService.getAccountEmissionTradingScheme(1L)).thenReturn(EmissionTradingScheme.EU_ETS_INSTALLATIONS);

        permitIssuanceRegistryEventPublisherService.publishRegistryEvent(payload,"request-id",1L);

        verifyNoInteractions(applicationEventPublisher);

    }

    @Test
    void permitGranted_isNotPublishedBecauseOfPermitType() {

        PermitIssuanceRequestPayload payload =
                PermitIssuanceRequestPayload.builder().determination(PermitIssuanceGrantDetermination.builder()
                                .type(DeterminationType.GRANTED).build())
                        .permitType(PermitType.HSE).build();


        permitIssuanceRegistryEventPublisherService.publishRegistryEvent(payload,"request-id",1L);

        verifyNoInteractions(applicationEventPublisher);

    }

    @Test
    void permitGranted_isNotPublishedBecauseOfPermitRejection() {

        PermitIssuanceRequestPayload payload =
                PermitIssuanceRequestPayload.builder().determination(PermitIssuanceRejectDetermination.builder()
                                .type(DeterminationType.REJECTED).build())
                        .permitType(PermitType.GHGE).build();


        permitIssuanceRegistryEventPublisherService.publishRegistryEvent(payload,"request-id",1L);

        verifyNoInteractions(applicationEventPublisher);

    }

}
