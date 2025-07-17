package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BDRUploadAttachmentService {


     private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(final Long requestTaskId,
                                 final String attachmentUuid,
                                 final String filename) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final BDRApplicationSubmitRequestTaskPayload requestTaskPayload =
                (BDRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getBdrAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
