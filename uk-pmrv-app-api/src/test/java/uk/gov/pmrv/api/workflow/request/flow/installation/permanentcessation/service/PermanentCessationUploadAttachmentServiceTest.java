package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PermanentCessationUploadAttachmentServiceTest {


    @Mock
    private RequestTaskService requestTaskService;

    @InjectMocks
    private PermanentCessationUploadAttachmentService uploadAttachmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadAttachment() {
        Long requestTaskId = 123L;
        String attachmentUuid = UUID.randomUUID().toString();
        String filename = "document.pdf";
        UUID attachmentUuidObj = UUID.fromString(attachmentUuid);

        PermanentCessationApplicationSubmitRequestTaskPayload payload = new PermanentCessationApplicationSubmitRequestTaskPayload();
        payload.setPermanentCessationAttachments(new HashMap<>());

        RequestTask requestTask = mock(RequestTask.class);
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTask.getPayload()).thenReturn(payload);

        uploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);

        assertEquals(filename, payload.getPermanentCessationAttachments().get(attachmentUuidObj));
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
    }
}