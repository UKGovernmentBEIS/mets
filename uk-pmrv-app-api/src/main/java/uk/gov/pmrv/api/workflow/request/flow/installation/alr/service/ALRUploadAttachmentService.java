package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ALRUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(final Long requestTaskId,
                                 final String attachmentUuid,
                                 final String filename) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final ALRApplicationSubmitRequestTaskPayload requestTaskPayload =
                (ALRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getAlrAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
