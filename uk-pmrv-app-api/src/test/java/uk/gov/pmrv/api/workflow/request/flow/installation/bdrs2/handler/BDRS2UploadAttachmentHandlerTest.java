package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2UploadAttachmentService;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BDRS2UploadAttachmentHandlerTest {

    @InjectMocks
    private BDRS2UploadAttachmentHandler handler;

    @Mock
    private BDRS2UploadAttachmentService attachmentService;


    @Test
    void uploadAttachment() {
        final Long requestTaskId = 1L;
        final String filename = "filename";
        final String attachmentUuid = UUID.randomUUID().toString();

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(attachmentService, times(1)).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }
}
