package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermanentCessationUploadAttachmentService {


     private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(final Long requestTaskId,
                                 final String attachmentUuid,
                                 final String filename) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PermanentCessationApplicationSubmitRequestTaskPayload requestTaskPayload =
                (PermanentCessationApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getPermanentCessationAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
