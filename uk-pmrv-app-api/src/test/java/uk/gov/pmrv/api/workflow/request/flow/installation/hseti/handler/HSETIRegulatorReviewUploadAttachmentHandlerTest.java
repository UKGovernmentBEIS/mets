package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIRegulatorReviewUploadAttachmentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HSETIRegulatorReviewUploadAttachmentHandlerTest {

    @InjectMocks
    private HSETIRegulatorReviewUploadAttachmentHandler handler;

    @Mock
    private HSETIRegulatorReviewUploadAttachmentService hseTiVerificationUploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String attachmentUuid = "attachment-uuid";
        String filename = "attachment.txt";

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(hseTiVerificationUploadAttachmentService).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        RequestTaskActionType type = handler.getType();
        assertEquals(RequestTaskActionType.HSE_TI_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT, type);
    }
}
