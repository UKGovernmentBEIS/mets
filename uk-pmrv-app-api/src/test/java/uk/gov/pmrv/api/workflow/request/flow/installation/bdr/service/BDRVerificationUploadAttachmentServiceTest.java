package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationVerificationSubmitRequestTaskPayload;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRVerificationUploadAttachmentServiceTest {

    @InjectMocks
    private BDRVerificationUploadAttachmentService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void uploadAttachment() {
        long requestTaskId = 1L;
        String attachmentUuid = UUID.randomUUID().toString();
        String filename = "filename";
        BDRApplicationVerificationSubmitRequestTaskPayload taskPayload =
            BDRApplicationVerificationSubmitRequestTaskPayload.builder()
                .build();

        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        service.uploadAttachment(requestTaskId, attachmentUuid, filename);

        assertThat(taskPayload.getVerificationAttachments().get(UUID.fromString(attachmentUuid))).isEqualTo(filename);
    }
}
