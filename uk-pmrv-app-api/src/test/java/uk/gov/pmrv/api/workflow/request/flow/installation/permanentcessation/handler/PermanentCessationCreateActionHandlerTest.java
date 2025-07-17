package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermanentCessationCreateActionHandlerTest {

    @InjectMocks
    private PermanentCessationCreateActionHandler handler;

    @Mock
    private StartProcessRequestService startProcessRequestService;


    @Test
    void process() {
        final String requestId = "PC00001-1";
        final Long accountId = 1L;
        final Year year = Year.of(Year.now().getValue());
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().payloadType(RequestCreateActionPayloadType.EMPTY_PAYLOAD).build();

        final AppUser appUser = AppUser.builder().userId("regulator").build();

        final RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMANENT_CESSATION)
                .accountId(accountId)
                .requestPayload(PermanentCessationRequestPayload.builder()
                        .payloadType(RequestPayloadType.PERMANENT_CESSATION_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .permanentCessation(PermanentCessation.builder().build())
                        .build())
                .build();

        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(Request.builder().id(requestId).build());

        String result = handler.process(accountId, payload, appUser);

        assertThat(result).isEqualTo(requestId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void getRequestCreateActionType() {
        assertThat(handler.getRequestCreateActionType()).isEqualTo(RequestCreateActionType.PERMANENT_CESSATION);
    }
}
