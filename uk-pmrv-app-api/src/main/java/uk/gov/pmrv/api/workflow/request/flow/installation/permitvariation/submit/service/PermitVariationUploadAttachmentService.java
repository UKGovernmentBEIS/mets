package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        PermitVariationRequestTaskPayload requestTaskPayload =
            (PermitVariationRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getPermitAttachments().put(UUID.fromString(attachmentUuid), filename);
    }

}
