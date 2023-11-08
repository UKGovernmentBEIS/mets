package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationDreAddCancelledRequestActionServiceTest {

    @InjectMocks
    private AviationDreAddCancelledRequestActionService service;

    @Mock
    private RequestService requestService;

    @Test
    void add() {
        final Request request = Request.builder().id("1")
                .payload(AviationDreUkEtsRequestPayload.builder().regulatorAssignee("regulator").build()).build();

        when(requestService.findRequestById("1")).thenReturn(request);

        service.add("1");

        verify(requestService, times(1))
                .addActionToRequest(request, null, RequestActionType.AVIATION_DRE_APPLICATION_CANCELLED, "regulator");
    }
}
