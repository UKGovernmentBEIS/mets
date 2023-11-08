package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreApplyUploadAttachmentService;

@ExtendWith(MockitoExtension.class)
class DreApplyUploadAttachmentHandlerTest {

	@InjectMocks
    private DreApplyUploadAttachmentHandler cut;

    @Mock
    private DreApplyUploadAttachmentService dreApplyUploadAttachmentService;
    
    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "filename";
        String attachmentUuid = UUID.randomUUID().toString();

        //invoke
        cut.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(dreApplyUploadAttachmentService, times(1))
            .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }
    
    @Test
    void getTypes() {
        assertThat(cut.getType()).isEqualTo(RequestTaskActionType.DRE_APPLY_UPLOAD_ATTACHMENT);
    }
}
