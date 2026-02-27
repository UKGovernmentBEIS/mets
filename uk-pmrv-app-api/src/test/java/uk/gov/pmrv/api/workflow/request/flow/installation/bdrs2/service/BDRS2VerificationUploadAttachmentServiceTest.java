package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler.BDRS2ApplicationVerificationUploadAttachmentHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BDRS2VerificationUploadAttachmentServiceTest {

    @InjectMocks
    private BDRS2ApplicationVerificationUploadAttachmentHandler handler;

    @Mock
    private BDRS2VerificationUploadAttachmentService bdrs2VerificationUploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String attachmentUuid = "attachment-uuid";
        String filename = "attachment.txt";

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(bdrs2VerificationUploadAttachmentService).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        RequestTaskActionType type = handler.getType();
        assertEquals(RequestTaskActionType.BDRS2_VERIFICATION_UPLOAD_ATTACHMENT, type);
    }
}
