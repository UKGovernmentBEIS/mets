package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceApprovedEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

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

    @Test
    void publishRegistryEvent_whenApproved_publishesEmpIssuanceApprovedEvent() {
        String requestId = "1";
        Long accountId = 1L;

        EmpIssuanceUkEtsRequestPayload payload = mock(EmpIssuanceUkEtsRequestPayload.class, RETURNS_DEEP_STUBS);
        when(payload.getDetermination().getType()).thenReturn(EmpIssuanceDeterminationType.APPROVED);

        empIssuanceRegistryEventPublisherService.publishRegistryEvent(payload, requestId, accountId);

        ArgumentCaptor<EmpIssuanceApprovedEvent> eventCaptor = ArgumentCaptor.forClass(EmpIssuanceApprovedEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

        EmpIssuanceApprovedEvent published = eventCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(requestId, published.getRequestId());
        org.junit.jupiter.api.Assertions.assertEquals(accountId, published.getAccountId());
    }

    @Test
    void publishRegistryEvent_whenNotApproved_doesNotPublish() {
        String requestId = "1";
        Long accountId = 1L;

        EmpIssuanceUkEtsRequestPayload payload = mock(EmpIssuanceUkEtsRequestPayload.class, RETURNS_DEEP_STUBS);
        when(payload.getDetermination().getType()).thenReturn(EmpIssuanceDeterminationType.DEEMED_WITHDRAWN);

        empIssuanceRegistryEventPublisherService.publishRegistryEvent(payload, requestId, accountId);

        verify(applicationEventPublisher, never()).publishEvent(any(EmpIssuanceApprovedEvent.class));
    }
}
