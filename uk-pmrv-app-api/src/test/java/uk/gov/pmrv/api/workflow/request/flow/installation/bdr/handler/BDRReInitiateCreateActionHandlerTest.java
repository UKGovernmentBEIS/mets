package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRInitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRReInitiateCreateActionHandlerTest {


    @InjectMocks
    private BDRReInitiateCreateActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private StartProcessRequestService startProcessRequestService;


    @Test
    void process() {
        final long accountId = 1L;
        final String requestId = "BDR00001-2025";
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId).build();
        final ReportRelatedRequestCreateActionPayload actionPayload = ReportRelatedRequestCreateActionPayload.builder()
                .payloadType(RequestCreateActionPayloadType.REPORT_RELATED_REQUEST_CREATE_ACTION_PAYLOAD)
                .requestId(requestId)
                .build();

        Request request = Request
                .builder()
                .payload(BDRRequestPayload.builder().build())
                .metadata(BDRRequestMetadata.builder().build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        String actual = handler.process(accountId, actionPayload, user);

        // Verify
        Assertions.assertEquals(requestId, actual);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(startProcessRequestService, times(1))
                .reStartProcess(request, Map.of(BpmnProcessConstants.BDR_INITIATION_TYPE, BDRInitiationType.RE_INITIATED));
        verify(requestService, times(1))
                .addActionToRequest(request, null, RequestActionType.BDR_APPLICATION_RE_INITIATED, userId);
    }

    @Test
    void getRequestCreateActionType() {
        Assertions.assertEquals(RequestCreateActionType.BDR, handler.getRequestCreateActionType());
    }

}
