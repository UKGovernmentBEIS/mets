package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRVerificationUploadAttachmentService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ALRApplicationVerificationUploadAttachmentHandlerTest {

    @InjectMocks
    private ALRApplicationVerificationUploadAttachmentHandler handler;

    @Mock
    private ALRVerificationUploadAttachmentService alrVerificationUploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String attachmentUuid = "attachment-uuid";
        String filename = "attachment.txt";

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(alrVerificationUploadAttachmentService).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        RequestTaskActionType type = handler.getType();
        Assertions.assertEquals(RequestTaskActionType.ALR_VERIFICATION_UPLOAD_ATTACHMENT, type);
    }
}
