package uk.gov.pmrv.api.integration.registry.setoperator.aviation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.AviationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.service.AviationAccountEmpCommandOrchestrator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationSetOperatorIdAccountUpdateServiceTest {

    @InjectMocks
    private AviationSetOperatorIdAccountUpdateService service;

    @Mock
    private AviationAccountEmpCommandOrchestrator aviationAccountEmpCommandOrchestrator;

    @Mock
    private ApplicationEventPublisher publisher;

    @Test
    void notifyRegistryWithAccountUpdate_publishesEventWithEmpFromPendingApproval() {
        Long accountId = 1L;
        EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder().build();

        when(aviationAccountEmpCommandOrchestrator.getEmpFromPendingApprovalRequest(accountId)).thenReturn(emp);

        service.notifyRegistryWithAccountUpdate(accountId);

        verify(aviationAccountEmpCommandOrchestrator).getEmpFromPendingApprovalRequest(accountId);

        ArgumentCaptor<AviationAccountUpdatedRegistryEvent> eventCaptor =
                ArgumentCaptor.forClass(AviationAccountUpdatedRegistryEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());

        AviationAccountUpdatedRegistryEvent publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent);
        assertEquals(accountId, publishedEvent.getAccountId());
        assertEquals(emp, publishedEvent.getEmissionsMonitoringPlan());
    }

    @Test
    void notifyRegistryWithAccountUpdate_publishesEventWithNullEmpWhenNoPendingApproval() {
        Long accountId = 2L;

        when(aviationAccountEmpCommandOrchestrator.getEmpFromPendingApprovalRequest(accountId)).thenReturn(null);

        service.notifyRegistryWithAccountUpdate(accountId);

        verify(aviationAccountEmpCommandOrchestrator).getEmpFromPendingApprovalRequest(accountId);

        ArgumentCaptor<AviationAccountUpdatedRegistryEvent> eventCaptor =
                ArgumentCaptor.forClass(AviationAccountUpdatedRegistryEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());

        AviationAccountUpdatedRegistryEvent publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent);
        assertEquals(accountId, publishedEvent.getAccountId());
        assertNull(publishedEvent.getEmissionsMonitoringPlan());
    }
}