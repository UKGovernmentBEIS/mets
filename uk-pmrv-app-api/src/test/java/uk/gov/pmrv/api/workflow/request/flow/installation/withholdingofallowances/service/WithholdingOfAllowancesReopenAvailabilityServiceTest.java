package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesReopenAvailabilityServiceTest {

    @InjectMocks
    private WithholdingOfAllowancesReopenAvailabilityService service;

    @Mock
    private RequestService requestService;

    @Test
    void isWithholdReopenAvailable_false_no_requests() {
        Long accountId = 1L;
        when(requestService.getByAccountIdAndType(accountId, RequestType.WITHHOLDING_OF_ALLOWANCES))
                .thenReturn(Collections.emptyList());

        Boolean result = service.isWithholdReopenAvailable(accountId);

        assertThat(result).isFalse();
        verify(requestService, times(1)).getByAccountIdAndType(accountId, RequestType.WITHHOLDING_OF_ALLOWANCES);
    }

    @Test
    void isWithholdReopenAvailable_true_only_completed_requests() {
        Long accountId = 1L;
        Request request1 = Request.builder().status(RequestStatus.COMPLETED).build();
        Request request2 = Request.builder().status(RequestStatus.CANCELLED).build();
        
        when(requestService.getByAccountIdAndType(accountId, RequestType.WITHHOLDING_OF_ALLOWANCES))
                .thenReturn(List.of(request1, request2));

        Boolean result = service.isWithholdReopenAvailable(accountId);

        assertThat(result).isTrue();
        verify(requestService, times(1)).getByAccountIdAndType(accountId, RequestType.WITHHOLDING_OF_ALLOWANCES);
    }

    @Test
    void isWithholdReopenAvailable_false_has_in_progress_request() {
        Long accountId = 1L;
        Request request1 = Request.builder().status(RequestStatus.COMPLETED).build();
        Request request2 = Request.builder().status(RequestStatus.IN_PROGRESS).build();
        
        when(requestService.getByAccountIdAndType(accountId, RequestType.WITHHOLDING_OF_ALLOWANCES))
                .thenReturn(List.of(request1, request2));

        Boolean result = service.isWithholdReopenAvailable(accountId);

        assertThat(result).isFalse();
        verify(requestService, times(1)).getByAccountIdAndType(accountId, RequestType.WITHHOLDING_OF_ALLOWANCES);
    }
}
