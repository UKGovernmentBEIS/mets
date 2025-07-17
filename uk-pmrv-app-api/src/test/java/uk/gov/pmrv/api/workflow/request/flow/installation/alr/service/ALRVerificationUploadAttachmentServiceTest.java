package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSubmitRequestTaskPayload;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ALRVerificationUploadAttachmentServiceTest {

    @InjectMocks
    private ALRVerificationUploadAttachmentService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void uploadAttachment() {
        long requestTaskId = 1L;
        String attachmentUuid = UUID.randomUUID().toString();
        String filename = "filename";

        ALRApplicationVerificationSubmitRequestTaskPayload taskPayload =
                ALRApplicationVerificationSubmitRequestTaskPayload.builder()
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        service.uploadAttachment(requestTaskId, attachmentUuid, filename);

        assertThat(taskPayload.getVerificationAttachments().get(UUID.fromString(attachmentUuid))).isEqualTo(filename);
    }
}
