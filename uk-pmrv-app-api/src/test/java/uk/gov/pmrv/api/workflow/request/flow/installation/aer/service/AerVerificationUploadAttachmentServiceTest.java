package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmitRequestTaskPayload;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerVerificationUploadAttachmentServiceTest {

    @Mock
    private RequestTaskService requestTaskService;

    @InjectMocks
    private AerVerificationUploadAttachmentService service;

    @Test
    void uploadAttachment() {
        long requestTaskId = 1L;
        String attachmentUuid = UUID.randomUUID().toString();
        String filename = "filename";
        AerApplicationVerificationSubmitRequestTaskPayload taskPayload =
            AerApplicationVerificationSubmitRequestTaskPayload.builder()
                .build();

        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        service.uploadAttachment(requestTaskId, attachmentUuid, filename);

        assertThat(taskPayload.getVerificationAttachments().get(UUID.fromString(attachmentUuid))).isEqualTo(filename);
    }

}