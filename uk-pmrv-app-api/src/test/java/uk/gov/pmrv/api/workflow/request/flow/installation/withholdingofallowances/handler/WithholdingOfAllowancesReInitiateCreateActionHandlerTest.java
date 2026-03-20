package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesInitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesReCreateActionPayload;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesReInitiateCreateActionHandlerTest {

    @InjectMocks
    private WithholdingOfAllowancesReInitiateCreateActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
        Long accountId = 1L;
        String requestId = "REQ-001";
        String userId = "user-id";

        AppUser appUser = AppUser.builder().userId(userId).build();
        WithholdingOfAllowancesReCreateActionPayload payload = WithholdingOfAllowancesReCreateActionPayload.builder()
                .requestId(requestId)
                .build();

        Request request = Request.builder().id(requestId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        String result = handler.process(accountId, payload, appUser);

        assertEquals(requestId, result);

        verify(requestService).findRequestById(requestId);

        verify(startProcessRequestService).reStartProcess(request,
                Map.of(BpmnProcessConstants.WITHHOLDING_OF_ALLOWANCES_INITIATION_TYPE,
                        WithholdingOfAllowancesInitiationType.RE_INITIATED));

        verify(requestService).addActionToRequest(
                request,
                null,
                RequestActionType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_RE_INITIATED,
                userId);
    }

    @Test
    void getRequestCreateActionType() {
        assertEquals(RequestCreateActionType.WITHHOLDING_OF_ALLOWANCES_RE_INITIATE, handler.getRequestCreateActionType());
    }
}