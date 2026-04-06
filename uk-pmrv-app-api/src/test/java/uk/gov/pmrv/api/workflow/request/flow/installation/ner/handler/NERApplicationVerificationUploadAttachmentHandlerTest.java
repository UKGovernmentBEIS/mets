package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERVerificationUploadAttachmentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NERApplicationVerificationUploadAttachmentHandlerTest {

    @Mock
    private NERVerificationUploadAttachmentService service;

    @InjectMocks
    private NERApplicationVerificationUploadAttachmentHandler handler;

    @Test
    void uploadAttachment_shouldDelegateToService() {
        // Arrange
        Long requestTaskId = 1L;
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        String filename = "file.pdf";

        // Act
        handler.uploadAttachment(requestTaskId, uuid, filename);

        // Assert
        verify(service).uploadAttachment(requestTaskId, uuid, filename);
    }

    @Test
    void getType_shouldReturnCorrectType() {
        // Act
        RequestTaskActionType type = handler.getType();

        // Assert
        assertEquals(
                RequestTaskActionType.NER_VERIFICATION_UPLOAD_ATTACHMENT,
                type);
    }
}
