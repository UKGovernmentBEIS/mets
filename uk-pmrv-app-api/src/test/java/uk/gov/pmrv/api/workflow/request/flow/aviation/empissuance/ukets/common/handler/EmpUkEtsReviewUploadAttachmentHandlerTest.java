package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service.EmpUkEtsReviewUploadAttachmentService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmpUkEtsReviewUploadAttachmentHandlerTest {

    @InjectMocks
    private EmpUkEtsReviewUploadAttachmentHandler handler;

    @Mock
    private EmpUkEtsReviewUploadAttachmentService empUkEtsReviewUploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "name";
        String attachmentUuid = UUID.randomUUID().toString();

        //invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(empUkEtsReviewUploadAttachmentService, times(1))
            .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        assertThat(handler.getType()).isEqualTo(RequestTaskActionType.EMP_ISSUANCE_UKETS_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT);
    }
}