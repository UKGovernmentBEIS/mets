package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirUploadAttachmentService;

@ExtendWith(MockitoExtension.class)
class AirUploadAttachmentHandlerTest {

    @InjectMocks
    private AirUploadAttachmentHandler handler;

    @Mock
    private AirUploadAttachmentService service;

    @Test
    void uploadAttachment() {

        final Long requestTaskId = 1L;
        final String filename = "filename";
        final String attachmentUuid = UUID.randomUUID().toString();

        // Invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // Verify
        verify(service, times(1))
            .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        RequestTaskActionType actual = handler.getType();

        assertThat(actual).isEqualTo(RequestTaskActionType.AIR_UPLOAD_ATTACHMENT);
    }
}
