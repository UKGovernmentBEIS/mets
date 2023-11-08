package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.service.AviationDreUploadAttachmentService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationDreUploadAttachmentHandlerTest {

    @InjectMocks
    private AviationDreUploadAttachmentHandler aviationDreUploadAttachmentHandler;

    @Mock
    private AviationDreUploadAttachmentService aviationDreUploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "filename";
        String attachmentUuid = UUID.randomUUID().toString();

        aviationDreUploadAttachmentHandler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(aviationDreUploadAttachmentService, times(1))
            .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        assertEquals(RequestTaskActionType.AVIATION_DRE_UPLOAD_ATTACHMENT, aviationDreUploadAttachmentHandler.getType());
    }
}