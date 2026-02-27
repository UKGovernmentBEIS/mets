package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2RegulatorReviewUploadAttachmentServiceTest {

    @InjectMocks
    private BDRS2RegulatorReviewUploadAttachmentService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void uploadAttachment() {
        final Long requestTaskId = 1L;
        final String fileName = "review_attachment.pdf";
        final String attachmentUuid = UUID.randomUUID().toString();

        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .payload(BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload.builder().build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        service.uploadAttachment(requestTaskId, attachmentUuid, fileName);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);

        final BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload payload =
                (BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        assertThat(payload.getRegulatorReviewAttachments())
                .containsEntry(UUID.fromString(attachmentUuid), fileName);
    }
}
