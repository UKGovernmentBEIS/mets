package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2RegulatorReviewUploadAttachmentService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BDRS2RegulatorReviewUploadAttachmentHandlerTest {

    @InjectMocks
    private BDRS2RegulatorReviewUploadAttachmentHandler handler;

    @Mock
    private BDRS2RegulatorReviewUploadAttachmentService uploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String attachmentUuid = UUID.randomUUID().toString();
        String filename = "filename";

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(uploadAttachmentService, times(1)).uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        assertThat(handler.getType()).isEqualTo(RequestTaskActionType.BDRS2_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT);
    }
}
