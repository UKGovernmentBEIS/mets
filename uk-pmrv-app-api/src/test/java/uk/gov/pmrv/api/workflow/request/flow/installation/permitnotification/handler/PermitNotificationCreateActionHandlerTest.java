package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationCreateActionHandlerTest {

    @InjectMocks
    private PermitNotificationCreateActionHandler handler;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void getRequestCreateActionType() {
        assertThat(handler.getRequestCreateActionType()).isEqualTo(RequestCreateActionType.PERMIT_NOTIFICATION);
    }

    @Test
    void process() {
        Long accountId = 1L;
        RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().payloadType(RequestCreateActionPayloadType.EMPTY_PAYLOAD).build();
        AppUser appUser = AppUser.builder().userId("user").authorities(List.of(AppAuthority.builder()
                .accountId(accountId).build()))
                .build();

        RequestParams expectedRequestParams = RequestParams.builder()
                .type(RequestType.PERMIT_NOTIFICATION)
                .accountId(accountId)
                .requestPayload(PermitNotificationRequestPayload.builder()
                        .payloadType(RequestPayloadType.PERMIT_NOTIFICATION_REQUEST_PAYLOAD)
                        .operatorAssignee(appUser.getUserId())
                        .build())
                .requestMetadata(PermitNotificationRequestMetadata.builder()
                        .type(RequestMetadataType.PERMIT_NOTIFICATION)
                        .build())
                .build();

        when(startProcessRequestService.startProcess(expectedRequestParams)).thenReturn(Request.builder().id("requestId").build());

        final String requestId = handler.process(accountId, payload, appUser);

        verify(startProcessRequestService, times(1)).startProcess(expectedRequestParams);

        assertEquals("requestId", requestId);
    }
}
