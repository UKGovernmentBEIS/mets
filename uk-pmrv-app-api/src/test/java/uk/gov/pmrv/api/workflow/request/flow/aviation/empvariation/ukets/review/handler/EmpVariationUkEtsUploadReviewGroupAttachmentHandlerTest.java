package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsReviewUploadAttachmentService;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsUploadReviewGroupAttachmentHandlerTest {

	@InjectMocks
    private EmpVariationUkEtsUploadReviewGroupAttachmentHandler handler;

    @Mock
    private EmpVariationUkEtsReviewUploadAttachmentService uploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "filename";
        String attachmentUuid = UUID.randomUUID().toString();

        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(uploadAttachmentService, times(1))
            .uploadReviewGroupDecisionAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getTypes() {
        assertThat(handler.getType()).isEqualTo(RequestTaskActionType.EMP_VARIATION_UKETS_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT);
    }
}
