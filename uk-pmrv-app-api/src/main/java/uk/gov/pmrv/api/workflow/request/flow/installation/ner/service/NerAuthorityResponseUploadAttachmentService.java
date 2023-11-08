package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerAuthorityResponseRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class NerAuthorityResponseUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(final Long requestTaskId,
                                 final String attachmentUuid,
                                 final String filename) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NerAuthorityResponseRequestTaskPayload requestTaskPayload =
            (NerAuthorityResponseRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
