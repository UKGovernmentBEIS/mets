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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERInitiationType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERReInitiateCreateActionHandlerTest {

    @InjectMocks
    private NERReInitiateCreateActionHandler cut;

    @Mock
    private RequestService requestService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {

        Long accountId = 1L;
        AppUser appUser = AppUser.builder().userId("user").build();
        NERRequestCreateActionPayload payload =
                NERRequestCreateActionPayload.builder()
                        .requestId("REQ-1")
                        .build();

        NERRequestMetadata metadata = NERRequestMetadata.builder()
                .type(RequestMetadataType.NER)
                .build();

        Request request = Request.builder()
                .id("REQ-1")
                .metadata(metadata)
                .build();

        when(requestService.findRequestById("REQ-1")).thenReturn(request);

        String result = cut.process(accountId, payload, appUser);

        assertThat(result).isEqualTo("REQ-1");
        assertThat(metadata.getNerInitiationType())
                .isEqualTo(NERInitiationType.RE_INITIATED);

        verify(startProcessRequestService).reStartProcess(
                eq(request),
                eq(Map.of(
                        BpmnProcessConstants.NER_INITIATION_TYPE,
                        NERInitiationType.RE_INITIATED,
                        BpmnProcessConstants.SKIP_PAYMENT,
                        true))
        );

        verify(requestService).addActionToRequest(
                request,
                null,
                RequestActionType.NER_APPLICATION_RE_INITIATED,
                "user"
        );
    }

    @Test
    void getRequestCreateActionType() {
        assertThat(cut.getRequestCreateActionType())
                .isEqualTo(RequestCreateActionType.NER_RE_INITIATE);
    }
}
