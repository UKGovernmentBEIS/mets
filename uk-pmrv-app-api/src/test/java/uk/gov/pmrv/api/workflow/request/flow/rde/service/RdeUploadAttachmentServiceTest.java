package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class RdeUploadAttachmentServiceTest {

    @InjectMocks
    private RdeUploadAttachmentService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void uploadAttachment() {

        Long requestTaskId = 1L;
        String fileName = "name";
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(RdeForceDecisionRequestTaskPayload.builder().build())
            .build();
        String attachmentUuid = UUID.randomUUID().toString();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        service.uploadAttachment(requestTaskId, attachmentUuid, fileName);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);

        assertThat(requestTask.getPayload().getAttachments()).containsEntry(UUID.fromString(attachmentUuid),
            fileName);
    }
}
