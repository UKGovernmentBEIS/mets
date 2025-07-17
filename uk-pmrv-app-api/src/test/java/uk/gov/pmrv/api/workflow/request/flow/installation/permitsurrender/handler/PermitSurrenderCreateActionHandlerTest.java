package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.handler;

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
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderCreateActionHandlerTest {

    @InjectMocks
    private PermitSurrenderCreateActionHandler handler;
    
    @Mock
    private StartProcessRequestService startProcessRequestService;
    
    @Test
    void getRequestCreateActionType() {
        assertThat(handler.getRequestCreateActionType()).isEqualTo(RequestCreateActionType.PERMIT_SURRENDER);
    }
    
    @Test
    void process() {
        Long accountId = 1L;
        RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().payloadType(RequestCreateActionPayloadType.EMPTY_PAYLOAD).build();
        AppUser appUser = AppUser.builder().userId("user").authorities(List.of(AppAuthority.builder()
                .accountId(accountId).build()))
                .build();

        RequestParams expectedRequestParams = RequestParams.builder()
            .type(RequestType.PERMIT_SURRENDER)
            .accountId(accountId)
            .requestPayload(PermitSurrenderRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
                .operatorAssignee(appUser.getUserId())
                .build())
            .requestMetadata(PermitSurrenderRequestMetadata.builder()
                    .type(RequestMetadataType.PERMIT_SURRENDER)
                    .build())
            .build();
        
        when(startProcessRequestService.startProcess(expectedRequestParams)).thenReturn(Request.builder().id("requestId").build());

        final String requestId = handler.process(accountId, payload, appUser);

        verify(startProcessRequestService, times(1)).startProcess(expectedRequestParams);
        
        assertEquals("requestId", requestId);
    }
}
