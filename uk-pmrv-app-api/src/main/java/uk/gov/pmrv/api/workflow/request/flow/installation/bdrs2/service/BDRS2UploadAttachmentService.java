package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BDRS2UploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        BDRS2ApplicationSubmitRequestTaskPayload requestTaskPayload =
                (BDRS2ApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getBdrs2Attachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
