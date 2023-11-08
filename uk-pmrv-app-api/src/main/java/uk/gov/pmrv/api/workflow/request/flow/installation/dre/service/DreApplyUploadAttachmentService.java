package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DreApplyUploadAttachmentService {

	private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        DreApplicationSubmitRequestTaskPayload requestTaskPayload = (DreApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.getDreAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
