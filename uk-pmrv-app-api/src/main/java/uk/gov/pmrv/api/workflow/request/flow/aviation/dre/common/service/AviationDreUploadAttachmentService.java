package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreApplicationRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AviationDreUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        AviationDreApplicationRequestTaskPayload requestTaskPayload = (AviationDreApplicationRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.getDreAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
