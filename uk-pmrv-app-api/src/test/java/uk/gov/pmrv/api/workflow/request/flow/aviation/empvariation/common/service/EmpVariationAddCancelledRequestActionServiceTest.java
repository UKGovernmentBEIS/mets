package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationAddCancelledRequestActionServiceTest {

	@InjectMocks
    private EmpVariationAddCancelledRequestActionService service;

    @Mock
    private RequestService requestService;

    @Test
    void add() {

        final Request request = Request.builder()
            .id("1")
            .payload(EmpVariationUkEtsRequestPayload.builder().operatorAssignee("operator").build())
            .build();

        when(requestService.findRequestById("1")).thenReturn(request);

        service.add("1", RoleTypeConstants.OPERATOR);

        verify(requestService, times(1)).addActionToRequest(request,
            null,
            RequestActionType.EMP_VARIATION_APPLICATION_CANCELLED,
            "operator");
    }
}
