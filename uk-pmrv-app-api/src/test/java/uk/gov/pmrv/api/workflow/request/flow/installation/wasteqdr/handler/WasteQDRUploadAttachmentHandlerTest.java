package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRUploadAttachmentService;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WasteQDRUploadAttachmentHandlerTest {

    @InjectMocks
    private WasteQDRUploadAttachmentHandler handler;

    @Mock
    private WasteQDRUploadAttachmentService attachmentService;


    @Test
    void uploadAttachment() {
        final Long requestTaskId = 1L;
        final String filename = "filename";
        final String attachmentUuid = UUID.randomUUID().toString();

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(attachmentService, times(1)).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }
}
