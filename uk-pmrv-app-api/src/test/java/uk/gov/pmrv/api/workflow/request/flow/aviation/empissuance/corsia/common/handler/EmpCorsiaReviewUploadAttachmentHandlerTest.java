package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.service.EmpCorsiaReviewUploadAttachmentService;

@ExtendWith(MockitoExtension.class)
class EmpCorsiaReviewUploadAttachmentHandlerTest {

    @InjectMocks
    private EmpCorsiaReviewUploadAttachmentHandler handler;

    @Mock
    private EmpCorsiaReviewUploadAttachmentService empCorsiaReviewUploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "name";
        String attachmentUuid = UUID.randomUUID().toString();

        //invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(empCorsiaReviewUploadAttachmentService, times(1))
            .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        assertThat(handler.getType()).isEqualTo(RequestTaskActionType.EMP_ISSUANCE_CORSIA_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT);
    }
}