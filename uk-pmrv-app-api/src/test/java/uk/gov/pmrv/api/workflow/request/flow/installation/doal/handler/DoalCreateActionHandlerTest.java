package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalCreateActionHandlerTest {

    @InjectMocks
    private DoalCreateActionHandler handler;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
        final String requestId = "DOAL00001-2023-1";
        final Long accountId = 1L;
        final Year year = Year.of(2023);
        final DoalRequestCreateActionPayload payload = DoalRequestCreateActionPayload.builder()
                .payloadType(RequestCreateActionPayloadType.DOAL_REQUEST_CREATE_ACTION_PAYLOAD)
                .year(year)
                .build();
        final AppUser appUser = AppUser.builder().userId("regulator").build();

        final RequestParams requestParams = RequestParams.builder()
                .type(RequestType.DOAL)
                .accountId(accountId)
                .requestMetadata(DoalRequestMetadata.builder()
                        .type(RequestMetadataType.DOAL)
                        .year(year)
                        .build())
                .requestPayload(DoalRequestPayload.builder()
                        .payloadType(RequestPayloadType.DOAL_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .reportingYear(year)
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
        assertThat(handler.getRequestCreateActionType()).isEqualTo(RequestCreateActionType.DOAL);
    }
}
