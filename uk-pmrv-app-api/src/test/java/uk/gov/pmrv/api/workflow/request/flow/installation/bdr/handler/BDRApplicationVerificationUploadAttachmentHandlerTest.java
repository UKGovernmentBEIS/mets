package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRVerificationUploadAttachmentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationVerificationUploadAttachmentHandlerTest {

    @InjectMocks
    private BDRApplicationVerificationUploadAttachmentHandler handler;

    @Mock
    private BDRVerificationUploadAttachmentService bdrVerificationUploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String attachmentUuid = "attachment-uuid";
        String filename = "attachment.txt";

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(bdrVerificationUploadAttachmentService).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        RequestTaskActionType type = handler.getType();
        assertEquals(RequestTaskActionType.BDR_VERIFICATION_UPLOAD_ATTACHMENT, type);
    }
}
