package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceApplicationRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermitSectionUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        PermitIssuanceApplicationRequestTaskPayload requestTaskPayload =
            (PermitIssuanceApplicationRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getPermitAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
