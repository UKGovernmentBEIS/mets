package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler.AerVerificationUploadAttachmentHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerVerificationUploadAttachmentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AerVerificationUploadAttachmentHandlerTest {

    @Mock
    private AerVerificationUploadAttachmentService aerVerificationUploadAttachmentService;

    @InjectMocks
    private AerVerificationUploadAttachmentHandler handler;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String attachmentUuid = "attachment-uuid";
        String filename = "attachment.txt";

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(aerVerificationUploadAttachmentService).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        RequestTaskActionType type = handler.getType();
        assertEquals(RequestTaskActionType.AER_VERIFICATION_UPLOAD_ATTACHMENT, type);
    }
}
