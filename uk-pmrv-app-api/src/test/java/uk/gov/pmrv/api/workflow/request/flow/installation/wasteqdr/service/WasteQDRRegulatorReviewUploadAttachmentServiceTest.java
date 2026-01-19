package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WasteQDRRegulatorReviewUploadAttachmentServiceTest {

    @InjectMocks
    private WasteQDRRegulatorReviewUploadAttachmentService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void uploadAttachment() {
        long requestTaskId = 1L;
        String attachmentUuid = UUID.randomUUID().toString();
        String filename = "filename";
        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        service.uploadAttachment(requestTaskId, attachmentUuid, filename);

        assertThat(taskPayload.getRegulatorReviewAttachments().get(UUID.fromString(attachmentUuid))).isEqualTo(filename);
    }
}
