package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationUploadAttachmentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PermanentCessationUploadAttachmentHandlerTest {

    @Mock
    private PermanentCessationUploadAttachmentService attachmentService;

    @InjectMocks
    private PermanentCessationUploadAttachmentHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadAttachment() {
        Long requestTaskId = 123L;
        String attachmentUuid = "abc-123-uuid";
        String filename = "document.pdf";

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(attachmentService, times(1)).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void testGetType() {
        RequestTaskActionType result = handler.getType();
        assertEquals(RequestTaskActionType.PERMANENT_CESSATION_UPLOAD, result);
    }
}
