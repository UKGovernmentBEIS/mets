package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETISubmitService;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HSETIUploadAttachmentHandlerTest {

    @InjectMocks
    private HSETIUploadAttachmentHandler handler;

    @Mock
    private HSETISubmitService submitService;


    @Test
    void uploadAttachment() {
        final Long requestTaskId = 1L;
        final String filename = "filename";
        final String attachmentUuid = UUID.randomUUID().toString();

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(submitService, times(1)).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

}
