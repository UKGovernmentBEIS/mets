package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerSectionUploadAttachmentService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationAerSectionUploadAttachmentHandlerTest {

    @InjectMocks
    private AviationAerSectionUploadAttachmentHandler aviationAerSectionUploadAttachmentHandler;

    @Mock
    private AviationAerSectionUploadAttachmentService aviationAerSectionUploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "filename";
        String attachmentUuid = UUID.randomUUID().toString();

        aviationAerSectionUploadAttachmentHandler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(aviationAerSectionUploadAttachmentService, times(1))
            .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        assertEquals(RequestTaskActionType.AVIATION_AER_UPLOAD_SECTION_ATTACHMENT, aviationAerSectionUploadAttachmentHandler.getType());
    }
}