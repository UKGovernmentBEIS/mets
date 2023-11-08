package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;


@ExtendWith(MockitoExtension.class)
class AviationAccountClosureCreateActionHandlerTest {

	@InjectMocks
    private AviationAccountClosureCreateActionHandler handler;

    @Mock
    private StartProcessRequestService startProcessRequestService;


    @Test
    void getType() {
        assertThat(handler.getType()).isEqualTo(RequestCreateActionType.AVIATION_ACCOUNT_CLOSURE);
    }

    @Test
    void process() {
        
        final Long accountId = 1L;
        final RequestCreateActionType type = RequestCreateActionType.AVIATION_ACCOUNT_CLOSURE;
        final RequestCreateActionEmptyPayload payload =
            RequestCreateActionEmptyPayload.builder().payloadType(RequestCreateActionPayloadType.EMPTY_PAYLOAD).build();
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId")
            .authorities(List.of(PmrvAuthority.builder().accountId(accountId).build()))
            .build();

        RequestParams expectedRequestParams = RequestParams.builder()
            .type(RequestType.AVIATION_ACCOUNT_CLOSURE)
            .accountId(accountId)
            .requestPayload(AviationAccountClosureRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_ACCOUNT_CLOSURE_REQUEST_PAYLOAD)
                .regulatorAssignee(pmrvUser.getUserId())
                .build())
            .build();

        when(startProcessRequestService.startProcess(expectedRequestParams))
            .thenReturn(Request.builder().id("requestId").build());


        final String requestId = handler.process(accountId, type, payload, pmrvUser);

        verify(startProcessRequestService, times(1)).startProcess(expectedRequestParams);

        assertEquals("requestId", requestId);
    }
}
