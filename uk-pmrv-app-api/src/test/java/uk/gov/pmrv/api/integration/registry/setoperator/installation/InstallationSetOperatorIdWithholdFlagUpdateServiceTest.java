package uk.gov.pmrv.api.integration.registry.setoperator.installation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.withholdflag.installation.request.WithholdFlagRegistryEvent;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowances;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationSetOperatorIdWithholdFlagUpdateServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InstallationSetOperatorIdWithholdFlagUpdateService service;

    @Test
    void notifyRegistryWithWithholdFlag_success() {
        Long accountId = 1L;
        Integer year = 2025;

        WithholdingOfAllowancesRequestPayload payload = WithholdingOfAllowancesRequestPayload.builder()
                .withholdingOfAllowances(WithholdingOfAllowances.builder().year(year).build())
                .build();

        Request request = Request.builder()
                .status(RequestStatus.COMPLETED)
                .payload(payload)
                .build();

        when(requestService.getByAccountIdAndType(accountId, RequestType.WITHHOLDING_OF_ALLOWANCES))
                .thenReturn(List.of(request));

        service.notifyRegistryWithWithholdFlag(accountId);

        verify(eventPublisher, times(1)).publishEvent(WithholdFlagRegistryEvent.builder()
                .withholdFlag(true)
                .accountId(accountId)
                .year(year)
                .build());
    }

    @Test
    void notifyRegistryWithWithholdFlag_throws_exception_on_multiple_requests() {
        Long accountId = 1L;
        Request request1 = Request.builder().build();
        Request request2 = Request.builder().build();

        when(requestService.getByAccountIdAndType(accountId, RequestType.WITHHOLDING_OF_ALLOWANCES))
                .thenReturn(List.of(request1, request2));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                service.notifyRegistryWithWithholdFlag(accountId));

        assertEquals(MetsErrorCode.INVALID_NUMBER_OF_WORKFLOWS, exception.getErrorCode());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void notifyRegistryWithWithholdFlag_no_event_on_cancelled_status() {
        Long accountId = 1L;
        Request request = Request.builder()
                .status(RequestStatus.CANCELLED)
                .build();

        when(requestService.getByAccountIdAndType(accountId, RequestType.WITHHOLDING_OF_ALLOWANCES))
                .thenReturn(List.of(request));

        service.notifyRegistryWithWithholdFlag(accountId);

        verify(eventPublisher, times(0)).publishEvent(any());
    }
}