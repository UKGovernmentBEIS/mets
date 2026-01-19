package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationAccountCreatedRegistryEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmpIssuanceRegistryEventPublisherServiceTest {

    @InjectMocks
    private EmpIssuanceRegistryEventPublisherService empIssuanceRegistryEventPublisherService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private EmpIssuanceRegistryIntegrationAddRequestActionService addRequestActionService;


    @Test
    void publishRegistryEvent_whenApproved_publishesEmpIssuanceApprovedEvent() {
        String requestId = "1";
        Long accountId = 1L;

        EmpIssuanceUkEtsRequestPayload payload = mock(EmpIssuanceUkEtsRequestPayload.class, RETURNS_DEEP_STUBS);
        when(payload.getDetermination().getType()).thenReturn(EmpIssuanceDeterminationType.APPROVED);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId))
                .thenReturn(Optional.of(EmissionsMonitoringPlanUkEtsDTO.builder()
                        .empContainer(EmissionsMonitoringPlanUkEtsContainer.builder().build()).build()));

        empIssuanceRegistryEventPublisherService.publishRegistryEvent(payload, requestId, accountId);

        ArgumentCaptor<AviationAccountCreatedRegistryEvent> eventCaptor = ArgumentCaptor.forClass(AviationAccountCreatedRegistryEvent.class);
        ArgumentCaptor<AviationAccountCreatedRegistryEvent> eventCaptor1 = ArgumentCaptor.forClass(AviationAccountCreatedRegistryEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

        AviationAccountCreatedRegistryEvent published = eventCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(requestId, published.getRequestId());
        org.junit.jupiter.api.Assertions.assertEquals(accountId, published.getAccountId());
    }

    @Test
    void publishRegistryEvent_whenNotApproved_doesNotPublish() {
        String requestId = "1";
        Long accountId = 1L;

        EmpIssuanceUkEtsRequestPayload payload = mock(EmpIssuanceUkEtsRequestPayload.class, RETURNS_DEEP_STUBS);
        when(payload.getDetermination().getType()).thenReturn(EmpIssuanceDeterminationType.DEEMED_WITHDRAWN);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId))
                .thenReturn(Optional.of(EmissionsMonitoringPlanUkEtsDTO.builder()
                        .empContainer(EmissionsMonitoringPlanUkEtsContainer.builder().build()).build()));

        empIssuanceRegistryEventPublisherService.publishRegistryEvent(payload, requestId, accountId);

        verify(applicationEventPublisher, never()).publishEvent(any(AviationAccountCreatedRegistryEvent.class));
        verify(addRequestActionService, never()).addRequestAction(any(AviationAccountCreatedRegistryEvent.class));

    }
}
