package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationVerificationSubmitRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BDRS2VerificationUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        BDRS2ApplicationVerificationSubmitRequestTaskPayload requestTaskPayload =
                (BDRS2ApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getVerificationAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
