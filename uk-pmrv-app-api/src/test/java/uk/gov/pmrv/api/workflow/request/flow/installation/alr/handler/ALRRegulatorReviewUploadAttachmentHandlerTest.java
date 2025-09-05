package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRRegulatorReviewUploadAttachmentService;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ALRRegulatorReviewUploadAttachmentHandlerTest {

    @InjectMocks
    private ALRRegulatorReviewUploadAttachmentHandler handler;

    @Mock
    private ALRRegulatorReviewUploadAttachmentService uploadAttachmentService;

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
        Assertions.assertEquals(RequestTaskActionType.ALR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT, handler.getType());
    }
}
