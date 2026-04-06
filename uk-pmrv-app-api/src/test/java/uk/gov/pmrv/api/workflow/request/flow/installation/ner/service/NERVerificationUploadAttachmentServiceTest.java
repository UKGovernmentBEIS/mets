package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSubmitRequestTaskPayload;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERVerificationUploadAttachmentServiceTest {

    @Mock
    private RequestTaskService requestTaskService;

    @InjectMocks
    private NERVerificationUploadAttachmentService service;

    @Test
    void uploadAttachment_shouldAddAttachmentToPayload() {
        // Arrange
        Long requestTaskId = 1L;
        String attachmentUuid = "123e4567-e89b-12d3-a456-426614174000";
        String filename = "file.pdf";

        NERApplicationVerificationSubmitRequestTaskPayload payload =
                new NERApplicationVerificationSubmitRequestTaskPayload();
        payload.setVerificationAttachments(new HashMap<>());

        RequestTask requestTask = new RequestTask();
        requestTask.setPayload(payload);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Act
        service.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // Assert
        UUID uuid = UUID.fromString(attachmentUuid);
        assertTrue(payload.getVerificationAttachments().containsKey(uuid));
        assertEquals(filename, payload.getVerificationAttachments().get(uuid));

        verify(requestTaskService).findTaskById(requestTaskId);
    }
}
