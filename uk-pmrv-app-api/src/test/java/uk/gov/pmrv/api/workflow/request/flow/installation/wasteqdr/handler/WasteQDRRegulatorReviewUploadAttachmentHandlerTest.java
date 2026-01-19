package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRRegulatorReviewUploadAttachmentService;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WasteQDRRegulatorReviewUploadAttachmentHandlerTest {

    @InjectMocks
    private WasteQDRRegulatorReviewUploadAttachmentHandler handler;

    @Mock
    private WasteQDRRegulatorReviewUploadAttachmentService uploadAttachmentService;

    @Test
    void process() {
        final Long requestTaskId = 1L;
        final String filename = "filename";
        final String attachmentUuid = UUID.randomUUID().toString();

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(uploadAttachmentService, times(1)).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        Assertions.assertEquals(RequestTaskActionType.WASTE_QDR_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT, handler.getType());
    }
}
