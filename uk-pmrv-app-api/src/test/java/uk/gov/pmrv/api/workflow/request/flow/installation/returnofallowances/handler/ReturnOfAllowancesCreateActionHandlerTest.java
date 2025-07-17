package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesCreateActionHandlerTest {

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @InjectMocks
    private ReturnOfAllowancesCreateActionHandler handler;

    @Test
    void process() {
        Long accountId = 123L;
        RequestCreateActionEmptyPayload payload = new RequestCreateActionEmptyPayload();
        AppUser appUser = new AppUser();

        RequestParams expectedParams = RequestParams.builder()
            .type(RequestType.RETURN_OF_ALLOWANCES)
            .accountId(accountId)
            .requestPayload(ReturnOfAllowancesRequestPayload.builder()
                .payloadType(RequestPayloadType.RETURN_OF_ALLOWANCES_REQUEST_PAYLOAD)
                .regulatorAssignee(appUser.getUserId())
                .build())
            .build();

        Request expectedRequest = new Request();
        expectedRequest.setId("requestId");

        when(startProcessRequestService.startProcess(expectedParams)).thenReturn(expectedRequest);

        String requestId = handler.process(accountId, payload, appUser);

        verify(startProcessRequestService).startProcess(expectedParams);
        assertEquals(expectedRequest.getId(), requestId);
    }

    @Test
    void getRequestCreateActionType() {
        RequestCreateActionType type = handler.getRequestCreateActionType();
        assertEquals(RequestCreateActionType.RETURN_OF_ALLOWANCES, type);
    }
}
