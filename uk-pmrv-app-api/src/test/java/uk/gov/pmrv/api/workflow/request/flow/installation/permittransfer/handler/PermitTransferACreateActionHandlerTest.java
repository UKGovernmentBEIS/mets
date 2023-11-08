package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;

@ExtendWith(MockitoExtension.class)
class PermitTransferACreateActionHandlerTest {

    @InjectMocks
    private PermitTransferACreateActionHandler handler;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void getType() {
        assertThat(handler.getType()).isEqualTo(RequestCreateActionType.PERMIT_TRANSFER_A);
    }

    @Test
    void process() {

        final Long accountId = 1L;
        final RequestCreateActionType type = RequestCreateActionType.PERMIT_TRANSFER_A;
        final RequestCreateActionEmptyPayload payload =
            RequestCreateActionEmptyPayload.builder().payloadType(RequestCreateActionPayloadType.EMPTY_PAYLOAD).build();
        final PmrvUser pmrvUser = PmrvUser.builder().userId("user").authorities(List.of(PmrvAuthority.builder()
                .accountId(accountId).build()))
            .build();

        final RequestParams expectedRequestParams = RequestParams.builder()
            .type(RequestType.PERMIT_TRANSFER_A)
            .accountId(accountId)
            .requestPayload(PermitTransferARequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_TRANSFER_A_REQUEST_PAYLOAD)
                .permitTransferDetails(PermitTransferDetails.builder().build())
                .operatorAssignee(pmrvUser.getUserId())
                .build())
            .build();

        when(startProcessRequestService.startProcess(expectedRequestParams)).thenReturn(Request.builder().id("requestId").build());

        final String requestId = handler.process(accountId, type, payload, pmrvUser);

        verify(startProcessRequestService, times(1)).startProcess(expectedRequestParams);

        assertEquals("requestId", requestId);
    }
}
