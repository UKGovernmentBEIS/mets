package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERInitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERRegulatorReviewSubmitService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERReInitiateCreateActionHandlerTest {

    @InjectMocks
    private NERReInitiateCreateActionHandler cut;

    @Mock
    private NERRegulatorReviewSubmitService nerRegulatorReviewSubmitService;

    @Mock
    private RequestService requestService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
        final long accountId = 1L;
        final String requestId = "NER00001-2025";
        final String userId = "userId";

        AppUser user = AppUser.builder()
                .userId(userId)
                .build();

        NERRequestCreateActionPayload actionPayload =
                NERRequestCreateActionPayload.builder()
                        .payloadType(RequestCreateActionPayloadType.NER_CREATE_ACTION_PAYLOAD)
                        .requestId(requestId)
                        .build();

        NerRequestPayload requestPayload = NerRequestPayload.builder().build();
        NERRequestMetadata requestMetadata = NERRequestMetadata.builder().build();

        Request request = Request.builder()
                .payload(requestPayload)
                .metadata(requestMetadata)
                .build();

        when(requestService.findRequestById(requestId))
                .thenReturn(request);

        String actual = cut.process(accountId, actionPayload, user);

        assertEquals(requestId, actual);

        verify(requestService).findRequestById(requestId);

        verify(nerRegulatorReviewSubmitService)
                .prepareRequestPayloadForReopening(requestPayload);

        verify(startProcessRequestService)
                .reStartProcess(
                        request,
                        Map.of(BpmnProcessConstants.SKIP_PAYMENT,
                                true,
                                BpmnProcessConstants.NER_INITIATION_TYPE,
                                NERInitiationType.RE_INITIATED));

        verify(requestService)
                .addActionToRequest(
                        request,
                        null,
                        RequestActionType.NER_APPLICATION_RE_INITIATED,
                        userId);

        assertEquals(
                NERInitiationType.RE_INITIATED,
                requestMetadata.getNerInitiationType());
    }

    @Test
    void getRequestCreateActionType() {
        assertThat(cut.getRequestCreateActionType())
                .isEqualTo(RequestCreateActionType.NER_RE_INITIATE);
    }
}
