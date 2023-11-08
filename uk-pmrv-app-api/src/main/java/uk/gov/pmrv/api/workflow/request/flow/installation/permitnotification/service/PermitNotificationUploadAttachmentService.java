package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationSubmitRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermitNotificationUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        PermitNotificationApplicationSubmitRequestTaskPayload requestTaskPayload =
            (PermitNotificationApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.getPermitNotificationAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
